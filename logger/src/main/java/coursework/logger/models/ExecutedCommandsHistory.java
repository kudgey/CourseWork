package coursework.logger.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutedCommandsHistory {
    @Id
    String id;
    HashMap<String, Object> args;
    @NotNull(message = "You must specify command name")
    String commandName;
    @NotNull(message = "You must specify user id")
    String userId;
    Date date = new Date();
}
