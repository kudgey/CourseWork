package coursework.commands.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import coursework.commands.logging.Method;
import coursework.commands.logging.Producer;
import coursework.commands.models.Argument;
import coursework.commands.models.Language;
import coursework.commands.repositories.ArgumentRepo;
import coursework.commands.repositories.CommandRepo;
import coursework.commands.repositories.LanguageRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RepositoryRestController
@RequestMapping("/api")
public class ArgumentController {
    private final ArgumentRepo repo;
    private final CommandRepo commandRepo;
    private final Producer producer;
    private String PATH = "/args";

    public ArgumentController(ArgumentRepo repo, CommandRepo commandRepo, Producer producer) {
        this.repo = repo;
        this.commandRepo = commandRepo;
        this.producer = producer;
    }

    @DeleteMapping("/args/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) throws JsonProcessingException {
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if (commandRepo.existsCommandsByArgumentsContains(repo.findArgumentById(id))) {
                msg = "There are commands that have this argument. You cannot delete it";
                status = HttpStatus.BAD_REQUEST;
            } else {
                msg = "Sucessfully deleted";
                repo.deleteById(id);
            }
        } catch (EntityNotFoundException ex){
            msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/" + id,
                Method.DELETE,
                status.value(),
                id,
                msg,
                "");
        return new ResponseEntity<>(msg, status);
    }

    @GetMapping("/args/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if (repo.existsById(id)) {
                obj = repo.findArgumentById(id);
            }
            else {
                obj = msg = "There is no argument with such id " + id;
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/" + id,
                Method.GET,
                status.value(),
                id,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }

    @GetMapping("/args")
    public ResponseEntity<Object> getAll() throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = repo.findAll();
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH,
                Method.GET,
                status.value(),
                null,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }
    @PutMapping("/args/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody Argument argument) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            Argument old = repo.findArgumentById(id);
            if (old == null) {
                obj = msg = "There is no argument with such id " + id;
                status = HttpStatus.NOT_FOUND;
            }
            else {
                if (argument.getName() == null || argument.getName().equals(""))
                    argument.setName(old.getName());
                else if (!argument.getName().equals(old.getName()) && repo.existsArgumentByName(argument.getName())) {
                    obj = msg = "Argument with such name already exists";
                    status = HttpStatus.BAD_REQUEST;
                }
                if (msg.equals("")) {
                    if (argument.getDescription() == null)
                        argument.setDescription(old.getDescription());
                    if (argument.getRegex_pattern() == null || argument.getRegex_pattern().equals(""))
                        argument.setRegex_pattern(old.getRegex_pattern());
                    if (argument.getType() == null || argument.getType().equals(""))
                        argument.setType(old.getType());

                    argument.setId(id);
                    repo.save(argument);
                    obj = argument;
                }
            }
        } catch (Exception e) {
            msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH+ "/" + id,
                Method.PUT,
                status.value(),
                id,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }
    @PostMapping("/args")
    public ResponseEntity<Object> create(@Valid @RequestBody Argument argument) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.CREATED;
        String id = "";
        try {
            if (argument.getName() == null || argument.getName().equals(""))
                obj = msg = "You need to specify an argument name";
            else if (repo.existsArgumentByName(argument.getName()))
                obj = msg = "Argument with such name already exists";

            if(argument.getRegex_pattern() == null || argument.getRegex_pattern().equals(""))
                obj = msg = "You need to specify a regular expression pattern";

            if(argument.getType() == null || argument.getType().equals(""))
                obj = msg = "You need to specify a type";

            if (msg.equals("")) {
                repo.save(argument);
                obj = argument;
                id = argument.getId();
            } else
                status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH,
                Method.POST,
                status.value(),
                id,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }
}
