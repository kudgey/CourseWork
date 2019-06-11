package coursework.commands.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.regex.Pattern;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Argument {
    @Id
    String id;

    @NotNull(message = "You must specify a name")
    @Indexed(unique = true)
    String name;

    String description;

    @NotNull(message = "You must specify a regular expression pattern")
    String regex_pattern;

    @NotNull(message = "You must specify a type")
    String type;

}
