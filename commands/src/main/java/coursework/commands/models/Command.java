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
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    @Id
    private String id;

    @NotNull(message = "You must specify a name")
    @Indexed(unique = true)
    private String name;

    private String description;

    private Double price;

    @NotNull(message = "You must specify code")
    private String code;

    @NotNull(message = "You must specify phrase")
    @NotEmpty(message = "You must specify phrase")
    @Indexed(unique = true)
    String phrase;

    @DBRef
    List<Argument> arguments;

    @NotNull(message = "You must specify lang")
    @DBRef
    Language lang;

    @DBRef
    Command parent;

    boolean open = false;

    @DBRef
    User user;
}
