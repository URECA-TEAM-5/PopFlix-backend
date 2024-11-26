package com.popflix.domain.movie.service.impl;

import com.popflix.domain.movie.dto.GetCreditsResponseDto;
import com.popflix.domain.movie.dto.GetRecommendedMovieResponseDto;
import com.popflix.domain.movie.dto.GetTMDBDetailsResponseDto;
import com.popflix.domain.movie.entity.*;
import com.popflix.domain.movie.repository.*;
import com.popflix.domain.movie.service.MovieApiService;
import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.personality.repository.GenreRepository;
import com.popflix.domain.user.repository.UserGenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieApiServiceImpl implements MovieApiService {

    private final UserGenreRepository userGenreRepository;

    private final String tmdbBaseUrl = "https://api.themoviedb.org/3/movie";

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final CastRepository castRepository;
    private final DirectorRepository directorRepository;
    private final MovieDirectorRepository movieDirectorRepository;
    private final MovieCastRepository movieCastRepository;


    @Transactional
    public void saveMovies() {
        for (int page = 1; page <= 1; page++) {
            String movieUrl = String.format("%s/popular?api_key=%s&page=%d&language=ko", tmdbBaseUrl, tmdbApiKey, page);
            GetTMDBDetailsResponseDto movieResponse = restTemplate.getForObject(movieUrl, GetTMDBDetailsResponseDto.class);

            if (movieResponse != null && movieResponse.getResults() != null) {
                for (GetTMDBDetailsResponseDto.Movie movieInfo : movieResponse.getResults()) {

                    String posterPath = movieInfo.getPosterPath() != null ?
                            "https://image.tmdb.org/t/p/w500" + movieInfo.getPosterPath() : "https://default-image-url.com/default.jpg";
                    LocalDate releaseDate = movieInfo.getReleaseDate();

                    // 영화 처리
                    Movie movie = movieRepository.findById(movieInfo.getId()).orElse(Movie.builder()
                            .title(movieInfo.getTitle())
                            .overview(movieInfo.getOverview())
                            .posterPath(posterPath)
                            .releaseDate(releaseDate)
                            .build());

                    movieRepository.save(movie);

                    // 장르 처리
                    if (movieInfo.getGenreIds() != null) {
                        for (Integer genreId : movieInfo.getGenreIds()) {
                            Genre genre = genreRepository.findById(Long.valueOf(genreId)).orElse(null);
                            if (genre != null) {
                                MovieGenre movieGenre = MovieGenre.builder()
                                        .movie(movie)
                                        .genre(genre)
                                        .build();
                                movieGenreRepository.save(movieGenre); // MovieGenre 저장
                            }
                        }
                    }

                    // 출연진과 감독 정보 처리 (credits API)
                    String creditsUrl = String.format("https://api.themoviedb.org/3/movie/%d/credits?api_key=%s&language=ko", movieInfo.getId(), tmdbApiKey);
                    GetCreditsResponseDto creditsResponse = restTemplate.getForObject(creditsUrl, GetCreditsResponseDto.class);

                    System.out.println("감독/출연진 응답 정보: " + creditsResponse);

                    if (creditsResponse != null) {
                        // 출연진 처리
                        if (creditsResponse.getCast() != null) {
                            for (GetCreditsResponseDto.CastMemberDto castMemberDto : creditsResponse.getCast()) {
                                // Cast 객체가 데이터베이스에 존재하는지 확인
                                Cast cast = castRepository.findByName(castMemberDto.getName()).orElse(
                                        Cast.builder()
                                                .name(castMemberDto.getName())
                                                .build()
                                );
                                // Cast 저장
                                castRepository.save(cast);

                                // MovieCast 저장
                                MovieCast movieCast = MovieCast.builder()
                                        .movie(movie)
                                        .cast(cast)
                                        .build();
                                movieCastRepository.save(movieCast); // MovieCast 저장
                            }
                        }

                        // 감독 처리
                        if (creditsResponse.getCrew() != null) {
                            for (GetCreditsResponseDto.CrewMemberDto crewMemberDto : creditsResponse.getCrew()) {
                                if ("Directing".equals(crewMemberDto.getDepartment()) && "Director".equals(crewMemberDto.getJob())) {
                                    // Director 객체 찾거나 생성
                                    Director director = directorRepository.findByName(crewMemberDto.getName())
                                            .orElseGet(() -> Director.builder()
                                                    .name(crewMemberDto.getName())
                                                    .build());
                                    // Director 저장
                                    directorRepository.save(director);

                                    // MovieDirector 저장
                                    MovieDirector movieDirector = MovieDirector.builder()
                                            .movie(movie)
                                            .director(director)
                                            .build();
                                    movieDirectorRepository.save(movieDirector);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 로그인하지 않은 유저에게 TMDB 이용 인기순으로 추천
    @Override
    public List<GetRecommendedMovieResponseDto> getRecommendedMovies() {
        String url = String.format("%s/popular?api_key=%s&language=ko", tmdbBaseUrl, tmdbApiKey);

        ResponseEntity<GetTMDBDetailsResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                GetTMDBDetailsResponseDto.class
        );

        List<GetRecommendedMovieResponseDto> recommendedMovies = new ArrayList<>();

        if (response.getBody() != null && response.getBody().getResults() != null) {
            for (GetTMDBDetailsResponseDto.Movie movie : response.getBody().getResults()) {
                Optional<Movie> localMovieOpt = movieRepository.findByTitle(movie.getTitle());
                if (localMovieOpt.isPresent()) {
                    Movie localMovie = localMovieOpt.get();
                    recommendedMovies.add(new GetRecommendedMovieResponseDto(
                            localMovie.getId(),
                            localMovie.getPosterPath()
                    ));
                }
            }
        }

        return recommendedMovies.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // 로그인한 유저에게 TMDB 이용 장르 기반 영화 추천
    @Override
    public List<GetRecommendedMovieResponseDto> getGenreBasedMovies(Long userId) {
        Long genreId = userGenreRepository.findGenreIdByUserId(userId);

        // TMDB API 이용 -> 장르 기반 추천 영화 가져오기
        String url = String.format("https://api.themoviedb.org/3/discover/movie?api_key=%s&with_genres=%d&language=ko", tmdbApiKey, genreId);

        ResponseEntity<GetTMDBDetailsResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                GetTMDBDetailsResponseDto.class
        );

        List<GetRecommendedMovieResponseDto> recommendedMovies = new ArrayList<>();

        if (response.getBody() != null && response.getBody().getResults() != null) {
            for (GetTMDBDetailsResponseDto.Movie movie : response.getBody().getResults()) {
                // 로컬 DB에서 제목으로 영화 검색
                Optional<Movie> localMovieOpt = movieRepository.findByTitle(movie.getTitle());
                if (localMovieOpt.isPresent()) {
                    Movie localMovie = localMovieOpt.get();
                    recommendedMovies.add(new GetRecommendedMovieResponseDto(
                            localMovie.getId(),
                            localMovie.getPosterPath()
                    ));
                }
            }
        }

        return recommendedMovies.stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}

