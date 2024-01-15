package hr.foi_fon.api.repositories;

import hr.foi_fon.api.models.Actor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActorRepository extends MongoRepository<Actor, ObjectId> {
}
