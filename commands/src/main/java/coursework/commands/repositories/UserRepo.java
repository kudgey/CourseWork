package coursework.commands.repositories;

import coursework.commands.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "user")
public interface UserRepo extends MongoRepository<User, String> {
    User findUserById(String id);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsUserByIdAndCommandsContains(String userId, String commandId);
    boolean existsUserByCommandsContains(String commandId);
}
