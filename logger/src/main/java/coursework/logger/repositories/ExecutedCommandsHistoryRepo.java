package coursework.logger.repositories;

import coursework.logger.models.ExecutedCommandsHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface ExecutedCommandsHistoryRepo extends MongoRepository<ExecutedCommandsHistory, String> {
}
