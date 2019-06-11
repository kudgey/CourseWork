package coursework.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursework.logger.models.CommandsAPIHistory;
import coursework.logger.models.ExecutedCommandsHistory;
import coursework.logger.repositories.CommandsAPIHistoryRepo;
import coursework.logger.repositories.ExecutedCommandsHistoryRepo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class Consumer {
    private final CommandsAPIHistoryRepo repo;
    private final ExecutedCommandsHistoryRepo executedCommandsHistoryRepo;

    public Consumer(CommandsAPIHistoryRepo repo, ExecutedCommandsHistoryRepo executedCommandsHistoryRepo) {
        this.repo = repo;
        this.executedCommandsHistoryRepo = executedCommandsHistoryRepo;
    }

    @RabbitListener(queues="commandsAPI")
    public void receivedMessageFound(String msg) throws IOException {
        CommandsAPIHistory history = new ObjectMapper().readValue(msg, CommandsAPIHistory.class);
        history.setDate(new Date());
        repo.save(history);
    }
    @RabbitListener(queues="executedCommands")
    public void receivedMessageFound1(String msg) throws IOException {
        ExecutedCommandsHistory history = new ObjectMapper().readValue(msg, ExecutedCommandsHistory.class);
        history.setDate(new Date());
        executedCommandsHistoryRepo.save(history);
    }
}
