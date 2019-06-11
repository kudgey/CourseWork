package coursework.commands.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import coursework.commands.Services.CBoxService;
import coursework.commands.logging.Method;
import coursework.commands.logging.Producer;
import coursework.commands.models.CBox;
import coursework.commands.models.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class  CBoxController {
    private final CBoxService service;
    private final Producer producer;

    public CBoxController(CBoxService service, Producer producer) {
        this.service = service;
        this.producer = producer;
    }
    String PATH = "/cbox";

    @GetMapping("/cbox")
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

    @GetMapping("/cbox/{id}")
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
    @GetMapping("/cbox/commands/{id}")
    public ResponseEntity<Object> getCommands(@PathVariable String id) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = service.get(id).getCommands().stream().map(Command::getId).toArray();
        } catch (EntityNotFoundException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/commands/" + id,
                Method.GET,
                status.value(),
                id,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }
    @GetMapping("/cbox/byuser/{userId}")
    public ResponseEntity<Object> getCBoxesByUser(@PathVariable String userId) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = service.getByUser(userId);
        } catch (EntityNotFoundException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } catch (IllegalArgumentException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/byuser/" + userId,
                Method.GET,
                status.value(),
                userId,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }
    @GetMapping("/cbox/byname/{name}")
    public ResponseEntity<Object> getByName(@PathVariable String name) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = service.getByName(name);
        } catch (EntityNotFoundException ex) {
            obj = msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        producer.sendMessage(
                PATH + "/" + name,
                Method.GET,
                status.value(),
                name,
                msg,
                "");
        return new ResponseEntity<>(obj, status);
    }

    @PostMapping(value = "/cbox", consumes = "application/json")
    public ResponseEntity create(@Valid @RequestBody CBoxJson cBoxJson) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.CREATED;
        String id = "";
        try {
            if(cBoxJson.getCBox() == null) {
                obj = msg = "You need to specify a command box";
                status = HttpStatus.BAD_REQUEST;
            } else {
                service.create(
                        cBoxJson.getCBox(),
                        cBoxJson.getCommands_ids()
                );
                obj = cBoxJson.getCBox();
                id = cBoxJson.getCBox().getId();
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

    @PutMapping("/cbox/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody CBoxJson cBoxJson) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if(cBoxJson.getCBox() == null) {
                obj = msg = "You need to specify a command box";
                status = HttpStatus.BAD_REQUEST;
            } else {
                service.update(
                        id,
                        cBoxJson.getCBox(),
                        cBoxJson.getCommands_ids()
                );
                obj = cBoxJson.getCBox();
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

    @DeleteMapping("/cbox/{id}")
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
    @PostMapping("/cbox/exist")
    public boolean boxExist(@RequestBody String id) {
        return service.existBox(id);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CBoxJson {
    CBox cBox;
    List<String> commands_ids;
}
