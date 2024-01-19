package hr.foi_fon.api.services;

import hr.foi_fon.api.dtos.MovieDto;
import hr.foi_fon.api.dtos.MovieGenresDto;
import hr.foi_fon.api.models.*;
import hr.foi_fon.api.repositories.*;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private UserRepository userRepository;

    public List<MovieDto> allMovies() {
        List<Movie> movies=movieRepository.findAll();
        return movies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MovieDto convertToDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDate_release(movie.getDate_release());
        dto.setGenres(convertToGenreDto(movie.getGenres()));
        dto.setDirector(convertToDirectorDto(movie.getDirector()));
        dto.setActors(convertToActorDto(movie.getActors()));
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setUrl(movie.getUrl());
        return dto;
    }

    private String convertToDirectorDto(ObjectId director){
        Optional<Director> optionalDirector = directorRepository.findById(director);
        return optionalDirector.get().getName();

    }


    private List<String> convertToActorDto(List<ObjectId> actors){
        List<String> actorStrings = actors.stream()
                .map(actorId ->{
                    Optional<Actor> optionalActor = actorRepository.findById(actorId);
                    return optionalActor.map(Actor::getName).orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return actorStrings;

    }
    private MovieGenresDto convertToGenreDto(GenreWrapper genres) {
        MovieGenresDto movieGenresDto = new MovieGenresDto();



        if (genres.getPrimary() != null) {

            Optional<Genre> optionalGenre = genreRepository.findById(genres.getPrimary());
            if (optionalGenre.isPresent()) {
                movieGenresDto.setPrimary(optionalGenre.get().getName());
            }
        }

        if (genres.getSecondary() != null && !genres.getSecondary().isEmpty()) {
            List<String> secondaryGenreNames = genres.getSecondary().stream()
                    .map(genreId -> {
                        Optional<Genre> optionalSecondaryGenre = genreRepository.findById(genreId);
                        return optionalSecondaryGenre.map(Genre::getName).orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            movieGenresDto.setSecondary(secondaryGenreNames);
        }

        return movieGenresDto;
    }

    public MovieDto getMovieDetails(String movieId, String token) {
        ObjectId objectId = new ObjectId(movieId);
        Optional<Movie> optionalMovie = movieRepository.findById(objectId);
        if(optionalMovie.isPresent()){
            Movie movie = optionalMovie.get();
            String userIdString=getUserIdFromToken(token);
            ObjectId userId = new ObjectId(userIdString);
            Optional<User> optionalUser = userRepository.findById(userId);
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                List<ObjectId> history = user.getHistory();
                if (!history.contains(objectId)) {
                    history.add(objectId);
                    user.setHistory(history);
                    userRepository.save(user);
                }
                return convertToDto(movie);
            }else{
                return null;
            }

        }else{
            return null;
        }
    }

    public String getUserIdFromToken(String token){
        String[] tokenParts = token.split("\\.");
        String payload = tokenParts[1];
        byte[] decodedPayload = java.util.Base64.getUrlDecoder().decode(payload);
        String decodedPayloadString = new String(decodedPayload, StandardCharsets.UTF_8);
        JSONObject payloadJson = new JSONObject(decodedPayloadString);

        String userIdFromToken = payloadJson.getString("userId");

        System.out.println("userIdFromToken from Token: " + userIdFromToken);
        System.out.println(payloadJson);

        return userIdFromToken;
    }

    public boolean addToWatchlist(String movieId, String token) {
        ObjectId objectId = new ObjectId(movieId);
        String userIdString=getUserIdFromToken(token);
        ObjectId userId = new ObjectId(userIdString);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            List<ObjectId> watchlist = user.getWatchlist();
            if (!watchlist.contains(objectId)) {
                watchlist.add(objectId);
                user.setWatchlist(watchlist);
                userRepository.save(user);
                return true;
            }
        }return false;

    }

    public boolean movieExists(String movieId) {
        ObjectId objectId = new ObjectId(movieId);
        Optional<Movie> optionalMovie = movieRepository.findById(objectId);
        if(optionalMovie!=null){
            return true;
        }else{
            return false;
        }

    }

    public List<MovieDto> getRecommendedMovies(String token) {
        String userIdString=getUserIdFromToken(token);
        ObjectId userId = new ObjectId(userIdString);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            List<ObjectId> preferences = user.getPreferences();
            List<Integer> favorite_decades = user.getFavorite_decades();
            Boolean longer_than_2h = user.isLonger_than_2h();
            List<ObjectId> history = user.getHistory();
            List<Movie> recommendedMovies = recommendMovies(preferences,favorite_decades,longer_than_2h, history);

            return recommendedMovies.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }else{
            return null;
        }

    }



    public List<Movie> recommendMovies(List<ObjectId> preferences, List<Integer> favorite_decades, Boolean longer_than_2h, List<ObjectId> history) {
        List<Movie> allMovies = movieRepository.findAll();
        List<ObjectId> historyPrimaryGenres = new ArrayList<>();
        double primaryGenreWeight = 7.0;
        double favoriteDecadeWeight = 5.0;
        double secondaryGenreWeight = 3.0;
        double historyPrimaryGenreWeight = 3.0;
        double historySecondaryGenreWeight = 1.0;
        double durationWeight = 1.0;

        Map<Movie, Double> movieScores = new HashMap<>();

        for (ObjectId historyMovieId : history) {
            Optional<Movie> optionalHistoryMovie = movieRepository.findById(historyMovieId);
            optionalHistoryMovie.ifPresent(historyMovie -> historyPrimaryGenres.add(historyMovie.getGenres().getPrimary()));
        }

        for (Movie movie : allMovies) {
            System.out.println("Movie" + movie.getTitle());
            double score = 0.0;

            if (preferences.contains(movie.getGenres().getPrimary())) {
                score += primaryGenreWeight;
                System.out.println("Primary Genre +7" );
            }

            if (movie.getGenres().getSecondary() != null) {
                for (ObjectId secondaryGenre : movie.getGenres().getSecondary()) {
                    if (preferences.contains(secondaryGenre)) {
                        score += secondaryGenreWeight;
                        System.out.println("Secondary Genre +3" );
                        break;
                    }else if(historyPrimaryGenres.contains(secondaryGenre)){
                        score+= historySecondaryGenreWeight;
                        System.out.println("History +1, user does not contain this genre in his preferences");
                        break;
                    }
                }
            }

            LocalDate movieReleaseDate = movie.getDate_release();
            int movieDecade = movieReleaseDate.getYear() / 10 * 10;

            if (favorite_decades.contains(movieDecade)) {
                score += favoriteDecadeWeight;
                System.out.println("Decade  +5" );
            }

            if ((longer_than_2h != null && longer_than_2h && movie.getDuration() > 120) ||
                    (longer_than_2h != null && !longer_than_2h && movie.getDuration() < 120)) {
                score += durationWeight;
                System.out.println("Duration  +1" );
            }

            if(historyPrimaryGenres.contains(movie.getGenres().getPrimary()) && !preferences.contains(movie.getGenres().getPrimary())){
                score+=historyPrimaryGenreWeight;
                System.out.println("History +3, user does not contain this genre in his preferences");
            }


            movieScores.put(movie, score);
            System.out.println("Score"+score );
            System.out.println("-------------" );
        }

        Map<Movie, Double> sortedMovieScores = movieScores.entrySet().stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(14)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return new ArrayList<>(sortedMovieScores.keySet());
    }



}
