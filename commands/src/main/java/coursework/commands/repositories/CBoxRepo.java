package coursework.commands.repositories;

import coursework.commands.models.CBox;
import coursework.commands.models.Command;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "cbox")
public interface CBoxRepo extends MongoRepository<CBox, String> {
    public CBox findCBoxByName(@Param("name") String name);
    public CBox findCBoxById(@Param("id") String id);
    public List<CBox> findAll();
    public boolean existsCBoxByName(String name);
    public boolean existsCBoxesByCommandsContains(Command command);
}
