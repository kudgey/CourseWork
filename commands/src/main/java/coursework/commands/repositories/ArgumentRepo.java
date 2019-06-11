package coursework.commands.repositories;

import coursework.commands.models.Argument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

public interface ArgumentRepo extends MongoRepository<Argument, String> {
    public Argument findArgumentByName(@Param("name") String name);
    public Argument findArgumentById(@Param("id") String id);
    public List<Argument> findArgumentsByIdIn(List<String> ids);
    public List<Argument> findAll();
    public boolean existsArgumentByName(String name);
}
