package coursework.commands.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import coursework.commands.Services.ComplexCommandService;
import coursework.commands.logging.Method;
import coursework.commands.logging.Producer;
import coursework.commands.models.CBox;
import coursework.commands.models.ComplexCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.print.DocFlavor;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ComplexCommandController {
    private final
    ComplexCommandService service;
    private final Producer producer;

    public ComplexCommandController(ComplexCommandService service, Producer producer) {
        this.service = service;
        this.producer = producer;
    }

    String PATH = "/complexcommand";

    @GetMapping("/complexcommand")
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

    @GetMapping("/complexcommand/{id}")
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

    @PostMapping(value = "/complexcommand", consumes = "application/json")
    public ResponseEntity create(@Valid @RequestBody ComplexCommandJson complexCommandJson) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.CREATED;
        String id = "";
        try {
            if(complexCommandJson.getComplexCommand() == null) {
                obj = msg = "You need to specify a complex command";
                status = HttpStatus.BAD_REQUEST;
            } else {
                service.create(
                        complexCommandJson.getComplexCommand(),
                        complexCommandJson.getCommands_ids(),
                        complexCommandJson.getReadyArgs()
                );
                obj = complexCommandJson.getComplexCommand();
                id = complexCommandJson.getComplexCommand().getId();
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

    @PutMapping("/complexcommand/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody ComplexCommandJson complexCommandJson) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if(complexCommandJson.getComplexCommand() == null) {
                obj = msg = "You need to specify a complex command";
                status = HttpStatus.BAD_REQUEST;
            } else {
                service.update(
                        id,
                        complexCommandJson.getComplexCommand(),
                        complexCommandJson.getCommands_ids(),
                        complexCommandJson.getReadyArgs()
                );
                obj = complexCommandJson.getComplexCommand();
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

    @DeleteMapping("/complexcommand/{id}")
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
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ComplexCommandJson {
    ComplexCommand complexCommand;
    List<String> commands_ids;
    HashMap<String, Object> readyArgs;
}