package coursework.commands.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Component
public class Producer {
    private final RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String path, Method method, Integer statusCode, String objectId, String message, String userId) throws JsonProcessingException {
        CommandsAPIHistory history = new CommandsAPIHistory(method, objectId, path, statusCode, message, userId);
        rabbitTemplate.convertAndSend("commandsAPI", new ObjectMapper().writeValueAsString(history));
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CommandsAPIHistory {
    Method method;
    String objectId;
    String path;
    Integer statusCode;
    String message;
    String userId;
}

