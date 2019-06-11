package coursework.commands.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.org.apache.xpath.internal.Arg;
import coursework.commands.Services.CommandService;
import coursework.commands.Services.ComplexCommandService;
import coursework.commands.logging.Method;
import coursework.commands.logging.Producer;
import coursework.commands.models.Argument;
import coursework.commands.models.Command;
import coursework.commands.models.ComplexCommand;
import coursework.commands.models.Language;
import coursework.commands.repositories.ArgumentRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CommandController {
    private final
    CommandService service;
    private final ArgumentRepo argumentRepo;
    private final ComplexCommandService complexCommandService;
    private final Producer producer;

    public CommandController(CommandService service, Producer producer, ComplexCommandService complexCommandService, ArgumentRepo argumentRepo) {
        this.service = service;
        this.producer = producer;
        this.complexCommandService = complexCommandService;
        this.argumentRepo = argumentRepo;
    }
    String PATH = "/commands";

    @GetMapping("/commands")
    public ResponseEntity getAll() throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = service.getAll();
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
    @GetMapping("/commands/byuser/{userId}")
    public ResponseEntity getAllByUser(@PathVariable String userId) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = service.getAllByUser(userId);
        } catch (EntityNotFoundException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/" + userId,
                Method.GET,
                status.value(),
                null,
                msg,
                userId);
        return new ResponseEntity<>(obj, status);
    }

    @GetMapping("/commands/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = service.get(id);
        } catch (EntityNotFoundException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
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

    @PostMapping(value = "/commands", consumes = "application/json")
    public ResponseEntity create(@Valid @RequestBody CommandJson commandJson) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.CREATED;
        String id = "";
        try {
            if(commandJson.getCommand() == null) {
                obj = msg = "You need to specify a command";
                status = HttpStatus.BAD_REQUEST;
            } else {
                service.create(
                        commandJson.getCommand(),
                        commandJson.getArgs_ids(),
                        commandJson.getLang_id(),
                        commandJson.getParent_id(),
                        commandJson.getUser_id()
                );
                obj = commandJson.getCommand();
                id = commandJson.getCommand().getId();
            }
        }
        catch (IllegalArgumentException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.BAD_REQUEST;
        }
        catch (Exception e) {
            obj = msg = e.getMessage();
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

    @PutMapping("/commands/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody CommandJson commandJson) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if(commandJson.getCommand() == null) {
                obj = msg = "You need to specify a command";
                status = HttpStatus.BAD_REQUEST;
            } else {
                service.update(
                        id,
                        commandJson.getCommand(),
                        commandJson.getArgs_ids(),
                        commandJson.getLang_id(),
                        commandJson.getParent_id(),
                        commandJson.getUser_id()
                );
                obj = commandJson.getCommand();
            }
        }
        catch (EntityNotFoundException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        }
        catch (IllegalArgumentException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.BAD_REQUEST;
        }
        catch (Exception e) {
            obj = msg = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/" + id,
                Method.PUT,
                status.value(),
                id,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }

    @DeleteMapping("/commands/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) throws JsonProcessingException {
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            service.delete(id);
            msg = "Successfully deleted";
        }
        catch (EntityNotFoundException ex) {
            msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        }
        catch (IllegalArgumentException ex) {
            msg = ex.getMessage();
            status = HttpStatus.BAD_REQUEST;
        }
        catch (Exception e) {
            msg = e.getMessage();
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

    @PostMapping("/commands/byphrase")
    public ResponseEntity<Object> getCommandByPhrase(@RequestBody PhraseUser phraseUser) {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            Command command = service.getByPhrase(phraseUser.getPhrase(), phraseUser.getUser_id());
            if (command == null){
                ComplexCommand complexCommand = complexCommandService.getByPhrase(phraseUser.getPhrase(), phraseUser.getUser_id());
                obj = new CommandExecuteJson(
                        null,
                        complexCommand.getCommands().stream().map(Command::getCode).toArray(String[]::new),
                        complexCommand.getName(),
                        complexCommand.getArgs());
            } else {
                List<ArgumentJson> args;
                if (command.getArguments() != null && !command.getArguments().isEmpty()) {
                    args = command
                            .getArguments()
                            .stream()
                            .map(a -> new ArgumentJson(
                                    a.getName(),
                                    a.getRegex_pattern(),
                                    a.getType()))
                            .collect(Collectors.toList());
                } else {
                    args = null;
                }
                obj = new CommandExecuteJson(args, new String[]{command.getCode()}, command.getName(), null);
            }
        }
        catch (IllegalArgumentException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        }
        catch (Exception e) {
            obj = msg = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }
    @PostMapping("commands/exist")
    public boolean commandsExist(@RequestBody String[] ids) {
        return service.existCommands(ids);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CommandJson {
    Command command;
    List<String> args_ids;
    String lang_id;
    String parent_id;
    String user_id;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CommandExecuteJson {
    List<ArgumentJson> args;
    String[] codes;
    String name;
    Map<String, Object> readyArgs;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ArgumentJson {
    String name;
    String regex_pattern;
    String type;
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class PhraseUser {
    String phrase;
    String user_id;
}