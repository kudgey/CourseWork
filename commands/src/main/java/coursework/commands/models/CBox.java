package coursework.commands.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CBox {
    @Id
    String id;

    @NotNull(message = "You must specify a name")
    @Indexed(unique = true)
    String name;

    String description;

    Double price;

    int n_commands = 0;

    @NotNull(message = "You must specify commands")
    @NotEmpty(message = "You must specify commands")
    @DBRef
    List<Command> commands;
}
