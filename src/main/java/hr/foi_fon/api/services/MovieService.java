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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
}
