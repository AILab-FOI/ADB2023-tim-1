package hr.foi_fon.api.repositories;

import hr.foi_fon.api.models.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, ObjectId> {
    List<Movie> findByTitleContainingIgnoreCase(String movieTitle);

    List<Movie> findByGenresPrimary(ObjectId genreId);
}
