package coursework.commands.repositories;

import coursework.commands.models.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

public interface LanguageRepo extends MongoRepository<Language, String> {
    public Language findLanguageByName(@Param("name") String name);
    public Language findLanguageByShortName(@Param("shortName") String shortName);
    public Language findLanguageById(@Param("id") String id);
    public List<Language> findAll();
    public boolean existsLanguageByName(String name);
}
