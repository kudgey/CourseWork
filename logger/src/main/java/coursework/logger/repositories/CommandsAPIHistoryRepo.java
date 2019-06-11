package coursework.logger.repositories;

import coursework.logger.models.CommandsAPIHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommandsAPIHistoryRepo extends MongoRepository<CommandsAPIHistory, String> {
}
