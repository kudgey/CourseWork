package coursework.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "commands", configuration = FeignClientConfiguration.class)
@RequestMapping("/api")
public interface CommandsClient {
    @GetMapping("/cbox/byname/{name}")
    public ResponseEntity<Object> getByName(@PathVariable String name);
    @PostMapping("commands/exist")
    public boolean commandsExist(@RequestBody String[] ids);
    @GetMapping("/commands/{id}")
    public ResponseEntity<Object> getCommand(@PathVariable String id);
    @GetMapping("/cbox/commands/{id}")
    public ResponseEntity<Object> getCBoxCommands(@PathVariable String id);
    @PostMapping("/cbox/exist")
    public boolean boxExist(@RequestBody String id);
}

