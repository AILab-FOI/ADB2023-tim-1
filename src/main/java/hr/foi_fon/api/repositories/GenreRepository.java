package hr.foi_fon.api.repositories;

import hr.foi_fon.api.models.Genre;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenreRepository extends MongoRepository<Genre, ObjectId>{

}
