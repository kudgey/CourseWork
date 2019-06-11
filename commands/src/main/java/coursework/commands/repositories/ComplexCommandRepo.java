package coursework.commands.repositories;

import coursework.commands.models.Command;
import coursework.commands.models.ComplexCommand;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComplexCommandRepo extends MongoRepository<ComplexCommand, String> {
    public ComplexCommand findComplexCommandById(String id);
    public ComplexCommand findComplexCommandByPhrase(String phrase);
    public boolean existsComplexCommandByName(String name);
    public boolean existsComplexCommandByPhrase(String phrase);

    public boolean existsComplexCommandsByCommandsContains(Command command);
}
