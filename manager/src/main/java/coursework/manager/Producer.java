package coursework.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Component
public class Producer {
    private final RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(HashMap<String, Object> args, String commandName, String userId) throws JsonProcessingException {
        ExecutedCommandHistory history = new ExecutedCommandHistory(args, commandName, userId);
        rabbitTemplate.convertAndSend("executedCommands", new ObjectMapper().writeValueAsString(history));
    }

}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class ExecutedCommandHistory {
    HashMap<String, Object> args;
    String commandName;
    String userId;
}
