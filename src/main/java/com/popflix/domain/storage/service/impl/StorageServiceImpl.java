package com.popflix.domain.storage.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.storage.dto.*;
import com.popflix.domain.storage.entity.MovieStorage;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.storage.entity.StorageLike;
import com.popflix.domain.storage.exception.AccessStorageDeniedException;
import com.popflix.domain.storage.exception.DuplicateMovieException;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.domain.storage.repository.MovieStorageRepository;
import com.popflix.domain.storage.repository.StorageLikeRepository;
import com.popflix.domain.storage.repository.StorageRepository;
import com.popflix.domain.storage.service.StorageService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final StorageLikeRepository storageLikeRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieStorageRepository movieStorageRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 보관함 생성
    @Transactional
    @Override
    public void createStorage(CreateStorageRequestDto storageRequest) {
        User user = userRepository.findById(storageRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(storageRequest.getUserId()));


        Storage storage = Storage.builder()
                .storageName(storageRequest.getStorageName())
                .storageOverview(storageRequest.getStorageOverview())
                .storageImage(storageRequest.getStorageImage())
                .user(user)
                .isPublic(false)
                .likeCount(0L)
                .movieCount(0L)
                .build();

        storageRepository.save(storage);
    }

    // 보관함 공개 여부 토글 (공개 ↔ 비공개)
    @Transactional
    @Override
    public void changeStatus(Long storageId, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        if (!storage.getUser().getUserId().equals(userId)) {
            throw new AccessStorageDeniedException("해당 보관함을 수정할 권한이 없습니다.");
        }

        storage.changeStatus();

        storageRepository.save(storage);
    }

    // 보관함 목록 조회
    @Override
    public List<GetStorageResponseDto> getStorageList(Long currentUserId, String sort) {
        // 정렬 기준에 따른 Sort 객체 생성
        Sort sorting;
        if ("popular".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "likeCount");
        } else if ("newest".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "createAt");
        } else {
            throw new IllegalArgumentException("Invalid sort type: " + sort);
        }

        // 로그인한 유저가 있다면 그 유저의 정보 가져오기
        boolean isUserLoggedIn = currentUserId != null;
        log.info("현재 사용자 로그인 여부!!!!!: " + currentUserId);

        List<Storage> storages;
        storages = storageRepository.findAll(sorting);

        // 보관함 목록 변환
        return storages.stream()
                .map(storage -> GetStorageResponseDto.builder()
                        .id(storage.getId())
                        .storageName(storage.getStorageName())
                        .username(storage.getUser().getName())
                        .movieCount(storage.getMovieCount())
                        .likeCount(storage.getLikeCount())
                        .isLiked(isUserLoggedIn ? storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(storage.getId(), currentUserId, true) : false)
                        .storageImage(storage.getStorageImage())
                        .build())
                .collect(Collectors.toList());
    }

    // 보관함 상세 조회
    @Override
    public GetStorageDetailResponseDto getStorageDetail(Long storageId, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        List<Movie> movies = storage.getMovies();
        boolean isLiked = storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(storageId, userId, true);

        return new GetStorageDetailResponseDto(storage, movies, userId, isLiked);
    }

    // 만든 이의 다름 보관함 조회
    @Override
    public List<GetStorageCreatorResponseDto> getOtherStoragesByCreator(Long storageId, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        User creator = storage.getUser();

        List<Storage> otherStorages = storageRepository.findByUserAndIdNot(creator, storageId);

        return otherStorages.stream()
                .map(otherStorage -> GetStorageCreatorResponseDto.builder()
                        .id(otherStorage.getId())
                        .storageName(otherStorage.getStorageName())
                        .movieCount(otherStorage.getMovieCount())
                        .likeCount(otherStorage.getLikeCount())
                        .isLiked(storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(otherStorage.getId(), userId, true))
                        .storageImage(otherStorage.getStorageImage())
                        .build())
                .collect(Collectors.toList());
    }


    // 보관함에 영화 추가
    @Transactional
    @Override
    public void addMovieToStorage(Long storageId, AddMovieRequestDto requestDto, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보관함입니다."));

        if (!storage.getUser().getUserId().equals(userId)) {
            throw new AccessStorageDeniedException("해당 보관함을 수정할 권한이 없습니다.");
        }

        Movie movie = movieRepository.findById(requestDto.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영화입니다."));

        boolean isAlreadyAdded = storage.getMovies().stream()
                .anyMatch(existingMovie -> existingMovie.getId().equals(movie.getId()));

        if (isAlreadyAdded) {
            throw new DuplicateMovieException(movie.getTitle());
        }

        MovieStorage movieStorage = MovieStorage.builder()
                .storage(storage)
                .movie(movie)
                .build();

        movieStorageRepository.save(movieStorage);

        storage.addMovie();
        storageRepository.save(storage);
    }

    // 보관함에서 영화 삭제
    @Transactional
    @Override
    public void removeMovieFromStorage(Long storageId, Long movieId, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        if (!storage.getUser().getUserId().equals(userId)) {
            throw new AccessStorageDeniedException("해당 보관함을 수정할 권한이 없습니다.");
        }

        MovieStorage movieStorage = movieStorageRepository.findByStorageAndMovieId(storage, movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        movieStorageRepository.delete(movieStorage);
        storage.removeMovie();
        storageRepository.save(storage);
    }

    // 보관함 삭제
    @Transactional
    @Override
    public void deleteStorage(Long storageId, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        if (!storage.getUser().getUserId().equals(userId)) {
            throw new AccessStorageDeniedException("해당 보관함을 삭제할 권한이 없습니다.");
        }

        storage.getMovieStorages().forEach(MovieStorage::delete);

        storage.getStorageLikes().forEach(StorageLike::delete);

        storage.delete();

        storageRepository.save(storage);
    }

    @Transactional
    @Override
    public void updateStorageDetails(Long storageId, UpdateStorageRequestDto requestDto, MultipartFile storageImage, Long userId) throws IOException {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        if (!storage.getUser().getUserId().equals(userId)) {
            throw new AccessStorageDeniedException("해당 보관함을 수정할 권한이 없습니다.");
        }

        if (requestDto.getNewName() != null && !requestDto.getNewName().isEmpty()) {
            storage.changeStorageName(requestDto.getNewName());
        }

        if (requestDto.getNewOverview() != null && !requestDto.getNewOverview().isEmpty()) {
            storage.changeStorageOverview(requestDto.getNewOverview());
        }

        String storageImageUrl = null;
        if (storageImage != null && !storageImage.isEmpty()) {
            // Storage 이미지 업로드 처리
            storageImageUrl = uploadStorageImage(storageImage, storageId); // 파일 업로드 후 URL 반환
        }

        storage.updateStorageImage(storageImageUrl);

        storageRepository.save(storage);
    }

    private String uploadStorageImage(MultipartFile file, Long storageId) {
        try {
            // S3에 저장될 파일 경로 생성
            String fileName = "storage/" + storageId + "/" +
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 파일 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(
                    bucket, fileName, file.getInputStream(), metadata
            ));

            // 업로드된 파일의 URL 반환
            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("Storage 이미지 업로드에 실패했습니다.", e);
        }
    }
    // 내가 만든 보관함 목록 조회
    @Override
    public List<GetMyStorageResponseDto> getStoragesByCreator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Storage> storages = storageRepository.findByUserAndIsDeletedFalse(user);

        List<GetMyStorageResponseDto> responseDtos = new ArrayList<>();
        for (Storage storage : storages) {
            List<Movie> movies = storage.getMovies();
            GetMyStorageResponseDto responseDto = new GetMyStorageResponseDto(storage, movies);
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    // 내가 좋아요한 보관함 목록 조회
    @Override
    public List<GetLikedStorageResponseDto> getLikedStorages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<StorageLike> likedStorages = storageLikeRepository.findByUserAndIsLikedTrue(user);

        return likedStorages.stream()
                .map(storageLike -> new GetLikedStorageResponseDto(storageLike.getStorage(), true))
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyTopStorageDto> getMonthlyTopStorages(int year, int month) {
        List<StorageLikeCountDto> topStorages = storageLikeRepository.findTopStoragesByLike(year, month);

        List<MonthlyTopStorageDto> responseDtos = new ArrayList<>();
        for (StorageLikeCountDto storageLikeCountDto : topStorages) {
            Long storageId = storageLikeCountDto.getStorageId();
            Long likeCount = storageLikeCountDto.getLikeCount();

            Storage storage = storageRepository.findById(storageId)
                    .orElseThrow(() -> new StorageNotFoundException(storageId));

            List<Movie> movies = storage.getMovies();

            Long movieCount = (long) movies.size();

            MonthlyTopStorageDto dto = new MonthlyTopStorageDto(storage, movies);
            dto.setLikeCount(likeCount);
            dto.setMovieCount(movieCount);

            responseDtos.add(dto);
        }

        return responseDtos;
    }


}
