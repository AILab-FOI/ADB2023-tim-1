package hr.foi_fon.api.controllers;

import hr.foi_fon.api.dtos.MovieDto;
import hr.foi_fon.api.models.Genre;
import hr.foi_fon.api.models.Movie;
import hr.foi_fon.api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
