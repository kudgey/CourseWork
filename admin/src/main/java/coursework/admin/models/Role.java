package coursework.admin.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Role {
    @Id
    String id;
    @NotNull(message = "You must specify a username")
    @Indexed(unique = true)
    String name;
    String description;
}
