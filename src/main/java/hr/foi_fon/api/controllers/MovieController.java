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

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    private ResponseEntity<List<MovieDto>> getAllMovies(){
        return new ResponseEntity<List<MovieDto>>(movieService.allMovies(), HttpStatus.OK);

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

}
