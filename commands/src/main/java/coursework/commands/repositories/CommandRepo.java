package coursework.commands.repositories;

import coursework.commands.models.Argument;
import coursework.commands.models.Command;
import coursework.commands.models.Language;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface CommandRepo extends MongoRepository<Command, String> {
    public Command findByName(String name);
    public Command findCommandById(String id);
    public List<Command> findCommandsByIdIn(List<String> ids);
    public List<Command> findAll();
    public List<Command> findAllByOpenIsTrue();
    public boolean existsCommandById(String id);
    public boolean existsCommandByName(String name);
    public boolean existsCommandsByLang(Language lang);
    public boolean existsCommandsByArgumentsContains(Argument arg);
    public boolean existsCommandsByPhrase(String phrase);
    public Command findByPhrase(String phrase);
}
