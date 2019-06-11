package coursework.logger.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandsAPIHistory {
    @Id
    String id;

    @NotNull(message = "You need to specify a method")
    Method method;

    String objectId;
    @NotNull(message = "You need to specify a path")
    String path;
    @NotNull(message = "You need to specify a status code")
    Integer statusCode;
    String message;
    Date date = new Date();
    String userId;
}

enum Method {
    POST, GET, PUT, DELETE
}
