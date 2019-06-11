package coursework.commands.controllers;

import coursework.commands.models.Argument;
import coursework.commands.models.Language;
import coursework.commands.repositories.ArgumentRepo;
import coursework.commands.repositories.CommandRepo;
import coursework.commands.repositories.LanguageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RepositoryRestController
@RequestMapping("/api")
public class LanguageController {
    private final LanguageRepo repo;
    private final CommandRepo commandRepo;

    public LanguageController(LanguageRepo repo, CommandRepo commandRepo) {
        this.repo = repo;
        this.commandRepo = commandRepo;
    }

    @DeleteMapping("/lang/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        try {
            if (commandRepo.existsCommandsByLang(repo.findLanguageById(id)))
                return new ResponseEntity<>("There are commands that have this language. You cannot delete it", HttpStatus.BAD_REQUEST);
            repo.deleteById(id);
        } catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @GetMapping("/lang/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) {
        try {
            if (repo.existsById(id))
                return new ResponseEntity<>(repo.findLanguageById(id), HttpStatus.OK);
            else
                return new ResponseEntity<>("There is no language with such id " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lang")
    public ResponseEntity<Object> getAll() {
        try {
            return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/lang/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody Language lang) {
        try {
            Language old = repo.findLanguageById(id);
            if (old == null)
                return new ResponseEntity<>("There is no language with such id " + id, HttpStatus.NOT_FOUND);
            else {
                if (lang.getName() == null || lang.getName().equals(""))
                    lang.setName(old.getName());
                else if (!lang.getName().equals(old.getName()) && repo.existsLanguageByName(lang.getName()))
                    return new ResponseEntity<>("Language with such name already exists", HttpStatus.BAD_REQUEST);
                if(lang.getDescription() == null)
                    lang.setDescription(old.getDescription());
                if(lang.getShortName() == null)
                    lang.setShortName(old.getShortName());

                lang.setId(id);
                repo.save(lang);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(lang, HttpStatus.OK);
    }
    @PostMapping("/lang")
    public ResponseEntity<Object> create(@Valid @RequestBody Language lang) {
        try {
            if (lang.getName() == null || lang.getName().equals(""))
                return new ResponseEntity<>("You need to specify a language name", HttpStatus.BAD_REQUEST);
            if (repo.existsLanguageByName(lang.getName()))
                return new ResponseEntity<>("Language with such name already exists", HttpStatus.BAD_REQUEST);
            repo.save(lang);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(lang, HttpStatus.OK);
    }
}
