package coursework.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursework.admin.models.Role;
import coursework.admin.models.User;
import coursework.admin.repositories.RoleRepo;
import coursework.admin.repositories.UserRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final
    UserRepo repo;
    private final RoleRepo roleRepo;
    private final
    PasswordEncoder encoder;
    private final CommandsClient client;

    public UserController(UserRepo repo, PasswordEncoder encoder, RoleRepo roleRepo, CommandsClient client) {
        this.repo = repo;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
        this.client = client;
    }
    @GetMapping("/users")
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

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) throws JsonProcessingException {
        Object obj;
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if (repo.existsById(id)) {
                obj = repo.findById(id);
            }
            else {
                obj = msg = "There is no user with such id " + id;
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            obj = msg = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity create(@RequestBody User user) throws JsonProcessingException {
        Object obj = null;
        String msg = "";
        HttpStatus status = HttpStatus.CREATED;
        String id = "";
        try {
            if (user.getUsername() == null || user.getUsername().equals(""))
                obj = msg = "You need to specify an username";
            else if (repo.existsByUsername(user.getUsername()))
                obj = msg = "User with such username already exists";

            if(user.getPassword() == null || user.getPassword().equals(""))
                obj = msg = "You need to specify a password";
            else {
                System.out.println(encoder.encode(user.getPassword()));
                user.setPassword(encoder.encode(user.getPassword()));
            }

            if (msg.equals("")) {
                user.setRole(roleRepo.findByName("Basic"));
                ResponseEntity<Object> resp = client.getByName("Basic");
                CBox cbox = new ObjectMapper().convertValue(resp.getBody(), CBox.class);
                user.setCommands(cbox.getCommands().stream().map(Command::getId).collect(Collectors.toList()));
                repo.save(user);
                obj = new UserRoleClient(user.getId(), user.getRole().getName());
            } else
                status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            msg = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity login(@RequestBody User user) throws JsonProcessingException {
        Object obj = null;
        HttpStatus status = HttpStatus.OK;
        String id = "";
        try {
            if (user.getUsername() == null || user.getUsername().equals(""))
                obj = "You need to specify an username";
            else if(!repo.existsByUsername(user.getUsername()))
                obj = "There is no such user";
            if(user.getPassword() == null || user.getPassword().equals(""))
                obj = "You need to specify a password";


            if (obj == null) {
                User foundUser = repo.findByUsername(user.getUsername());
                if (encoder.matches(user.getPassword(), foundUser.getPassword())){
                    obj = new UserRoleClient(foundUser.getId(), foundUser.getRole().getName());
                } else {
                    obj = "Password is invalid";
                    status = HttpStatus.BAD_REQUEST;
                }
            } else
                status = HttpStatus.BAD_REQUEST;
        } catch (IllegalAccessError e) {
            obj = e.getCause().getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) throws JsonProcessingException {
        String msg = "";
        HttpStatus status = HttpStatus.OK;
        try {
            if (repo.existsById(id)) {
                repo.deleteById(id);
            }
            else {
                msg = "There is no user with such id " + id;
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
    @PostMapping("users/addrole")
    public ResponseEntity<Object> addRole(@RequestBody UserRole userRole) throws JsonProcessingException {
        Object obj = "Added";
        HttpStatus status = HttpStatus.OK;
        try {
            Role role = roleRepo.findByName(userRole.getRoleName());
            if(role != null)
            {
                User user = repo.findByUsername(userRole.getUsername());
                if (user != null){
                    user.setRole(role);
                    repo.save(user);
                } else {
                    obj = "There is no user with such name " + userRole.getUsername();
                    status = HttpStatus.NOT_FOUND;
                }
            }
            else {
                obj = "There is no role with such name " + userRole.getRoleName();
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            obj = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }
    @PostMapping("/addcommands")
    public ResponseEntity<Object> addCommands(@RequestBody UserCommands userCommands) throws JsonProcessingException {
        Object obj = "Added";
        HttpStatus status = HttpStatus.OK;
        try {
            User user = repo.findByUsername(userCommands.getUsername());
            if(user != null)
            {
                if (client.commandsExist(userCommands.getCommands())){
                    Set<String> comms = new HashSet<>(user.getCommands());
                    comms.addAll(Arrays.asList(userCommands.getCommands()));
                    user.setCommands(new ArrayList<>(comms));
                    repo.save(user);
                } else {
                    obj = "There is no commands with such ids " + Arrays.toString(userCommands.getCommands());
                    status = HttpStatus.NOT_FOUND;
                }
            }
            else {
                obj = "There is no user with such name " + userCommands.getUsername();
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            obj = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }
    @PostMapping("/addbox")
    public ResponseEntity<Object> addCommandBox(@RequestBody UserBox userBox) throws JsonProcessingException {
        Object obj = "Added";
        HttpStatus status = HttpStatus.OK;
        try {
            Optional<User> userOptional = repo.findById(userBox.getUserId());
            if(userOptional.isPresent())
            {
                User user = userOptional.get();
                if (client.boxExist(userBox.getBoxId())){
                    Set<String> comms = new HashSet<>(user.getCommands());
                    ResponseEntity<Object> resp = client.getCBoxCommands(userBox.getBoxId());
                    List<String> commands = (List<String>) resp.getBody();

                    assert commands != null;
                    comms.addAll(commands);
                    user.setCommands(new ArrayList<>(comms));
                    repo.save(user);
                } else {
                    obj = "There is no command box with such id " + userBox.getBoxId();
                    status = HttpStatus.NOT_FOUND;
                }
            }
            else {
                obj = "There is no user with such id " + userBox.getUserId();
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            obj = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(obj, status);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserRole {
    String roleName;
    String username;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserRoleClient {
    String id;
    String role;

}
@Data
@AllArgsConstructor
@NoArgsConstructor
class UserCommands {
    String username;
    String[] commands;
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class UserBox {
    String userId;
    String boxId;
}



@Data
@AllArgsConstructor
@NoArgsConstructor
class CBox {
    String id;
    String name;
    String description;
    Double price;
    int n_commands = 0;
    List<Command> commands;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Command {
    private String id;
    private String name;
    private String description;
    private Double price;
    private String code;
    String phrase;
    List<Object> arguments;
    Object lang;
    Command parent;
    boolean open;
    User user;
}
