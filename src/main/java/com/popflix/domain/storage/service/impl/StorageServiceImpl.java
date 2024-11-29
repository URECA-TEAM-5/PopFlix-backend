package com.popflix.domain.storage.service.impl;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.GetStorageCreatorResponseDto;
import com.popflix.domain.storage.dto.GetStorageDetailResponseDto;
import com.popflix.domain.storage.dto.GetStorageResponseDto;
import com.popflix.domain.storage.entity.MovieStorage;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.storage.exception.AccessStorageDeniedException;
import com.popflix.domain.storage.exception.DuplicateStorageNameException;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.domain.storage.repository.MovieStorageRepository;
import com.popflix.domain.storage.repository.StorageLikeRepository;
import com.popflix.domain.storage.repository.StorageRepository;
import com.popflix.domain.storage.service.StorageService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final StorageLikeRepository storageLikeRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieStorageRepository movieStorageRepository;


    // 보관함 생성
    @Transactional
    @Override
    public void createStorage(CreateStorageRequestDto storageRequest) {
        User user = userRepository.findById(storageRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(storageRequest.getUserId()));

        if (storageRepository.existsByUserAndStorageName(user, storageRequest.getStorageName())) {
            throw new DuplicateStorageNameException(storageRequest.getStorageName());
        }

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
    public void changeStatus(Long storageId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        storage.changeStatus();

        storageRepository.save(storage);
    }

    // 보관함 목록 조회
    @Override
    public List<GetStorageResponseDto> getStorageList(Long userId, String sort) {
        // 정렬 기준에 따른 Sort 객체 생성
        Sort sorting;
        if ("popular".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "likeCount");
        } else if ("newest".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "createAt");
        } else {
            throw new IllegalArgumentException("Invalid sort type: " + sort);
        }

        // 정렬된 보관함 목록 조회
        List<Storage> storages = storageRepository.findAll(sorting);

        return storages.stream()
                .map(storage -> GetStorageResponseDto.builder()
                        .id(storage.getId())
                        .storageName(storage.getStorageName())
                        .username(storage.getUser().getName())
                        .movieCount(storage.getMovieCount())
                        .likeCount(storage.getLikeCount())
                        .isLiked(storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(storage.getId(), userId, true))
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
            throw new IllegalStateException("해당 영화는 이미 보관함에 추가되어 있습니다.");
        }

        MovieStorage movieStorage = MovieStorage.builder()
                .storage(storage)
                .movie(movie)
                .build();

        movieStorageRepository.save(movieStorage);

        storage.addMovie();
        storageRepository.save(storage);
    }

}
