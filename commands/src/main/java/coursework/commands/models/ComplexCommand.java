package coursework.commands.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplexCommand {
    @Id
    String id;

    @NotNull(message = "You must specify a name")
    @Indexed(unique = true)
    String name;

    String description;

    @NotNull(message = "You must specify commands")
    @NotEmpty(message = "You must specify commands")
    @DBRef
    List<Command> commands;

    Map<String, Object> args;

    @NotNull(message = "You must specify phrase")
    @NotEmpty(message = "You must specify phrase")
    @Indexed(unique = true)
    String phrase;
}
