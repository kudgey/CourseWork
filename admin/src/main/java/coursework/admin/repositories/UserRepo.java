package coursework.admin.repositories;

import coursework.admin.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "user")
public interface UserRepo extends MongoRepository<User, String> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
