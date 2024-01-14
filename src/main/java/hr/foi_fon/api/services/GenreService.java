package hr.foi_fon.api.services;

import hr.foi_fon.api.models.Genre;
import hr.foi_fon.api.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;


    public List<Genre> allGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genres;
    }
}
