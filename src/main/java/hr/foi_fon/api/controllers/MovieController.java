package hr.foi_fon.api.controllers;

import hr.foi_fon.api.dtos.MovieDto;
import hr.foi_fon.api.models.Genre;
import hr.foi_fon.api.models.Movie;
import hr.foi_fon.api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    private ResponseEntity<List<MovieDto>> getAllMovies(){
        return new ResponseEntity<List<MovieDto>>(movieService.allMovies(), HttpStatus.OK);

    }

    @GetMapping("/recommendations")
    private ResponseEntity<List<MovieDto>> getRecommendedMovies(@RequestHeader("Authorization") String token){
        return new ResponseEntity<List<MovieDto>>(movieService.getRecommendedMovies(token), HttpStatus.OK);
    }

    @GetMapping("/{movieId}")
    private ResponseEntity<?> getMovieDetails(@PathVariable String movieId,@RequestHeader("Authorization") String token){
        try{
            MovieDto movieDto = movieService.getMovieDetails(movieId, token);
            if(movieDto!=null){
                return new ResponseEntity<>(movieDto,HttpStatus.OK);
            }else{
                String errorMessage = "Movie with id: " + movieId + " is not found.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/watchlist/exists/{movieId}")
    public ResponseEntity<?> existsInWatchList(@PathVariable String movieId,@RequestHeader("Authorization") String token){
        ResponseEntity<Object> existsInWatchListResult=movieService.existsInWatchList(movieId,token);
        return new ResponseEntity<>(existsInWatchListResult.getBody(),existsInWatchListResult.getHeaders(),existsInWatchListResult.getStatusCode());
    }

    @PostMapping("/watchlist/{movieId}")
    public ResponseEntity<?> addToWatchlist(@PathVariable String movieId,@RequestHeader("Authorization") String token){
        try{
            boolean exists= movieService.movieExists(movieId);
            if(!exists){
                String errorMessage = "Movie with id: " + movieId + " is not found.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }else{


                     ResponseEntity<Object> addToWatchListResult = movieService.addToWatchlist(movieId,token);
                     return new ResponseEntity<>(addToWatchListResult.getBody(), addToWatchListResult.getHeaders(), addToWatchListResult.getStatusCode());


                 }


        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/watchlist/remove/{movieId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable String movieId, @RequestHeader("Authorization") String token){
        ResponseEntity<Object> removeFromWatchListResult=movieService.removeFromWatchList(movieId,token);
        return new ResponseEntity<>(removeFromWatchListResult.getBody(),removeFromWatchListResult.getHeaders(),removeFromWatchListResult.getStatusCode());
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchMovies(@RequestBody Map<String, Object> payload) {
        try {
            List<MovieDto> movieDtoList = movieService.searchMovies(payload);
            return new ResponseEntity<>(movieDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while processing the request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
