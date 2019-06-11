package coursework.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import coursework.admin.models.Role;
import coursework.admin.models.User;
import coursework.admin.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
public class RoleController {
    final
    RoleRepo repo;

    public RoleController(RoleRepo repo) {
        this.repo = repo;
    }
    @GetMapping("/roles")
    public ResponseEntity getAll() throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            obj = repo.findAll();
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if (repo.existsById(id)) {
                obj = repo.findById(id);
            }
            else {
                obj = msg = "There is no role with such id " + id;
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }

    @PostMapping(value = "/roles", consumes = "application/json")
    public ResponseEntity create(@RequestBody Role role) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.CREATED;
        String id = "";
        try {
            if (role.getName() == null || role.getName().equals(""))
                obj = msg = "You need to specify a name";
            else if (repo.existsByName(role.getName()))
                obj = msg = "Role with such name already exists";

            if (msg.equals("")) {
                repo.save(role);
                obj = role;
                id = role.getId();
            } else
                status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) throws JsonProcessingException {
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if (repo.existsById(id)) {
                repo.deleteById(id);
            }
            else {
                msg = "There is no role with such id " + id;
                status = HttpStatus.NOT_FOUND;
            }
        } catch (EntityNotFoundException ex){
            msg = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(msg, status);
    }
}
