package hr.foi_fon.api.repositories;

import hr.foi_fon.api.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByEmail(String email);
}
