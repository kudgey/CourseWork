package coursework.admin.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    String id;
    @Indexed(unique = true)
    @NotNull(message = "You must specify a username")
    String username;
    @NotNull(message = "You must specify a password")
    String password;
    List<String> commands;
    @DBRef
    Role role;
}
