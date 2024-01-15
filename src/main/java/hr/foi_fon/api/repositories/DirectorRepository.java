package hr.foi_fon.api.repositories;

import hr.foi_fon.api.models.Director;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DirectorRepository extends MongoRepository<Director, ObjectId> {

}
