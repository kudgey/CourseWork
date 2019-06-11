package coursework.commands.Services;

import coursework.commands.config.FeignClientConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "admin", configuration = FeignClientConfiguration.class)
@RequestMapping("/")
public interface AdminClient {
    @PostMapping("/addcommands")
    public ResponseEntity<Object> addCommands(@RequestBody UserCommands userCommands);
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserCommands {
    String username;
    String[] commands;
}