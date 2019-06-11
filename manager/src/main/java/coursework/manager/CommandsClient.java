package coursework.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "commands", configuration = FeignClientConfiguration.class)
@RequestMapping("/api")
public interface CommandsClient {
    @PostMapping("/commands/byphrase")
    public ResponseEntity<Object> getCommandByPhrase(@RequestBody PhraseUser phraseUser);
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class PhraseUser {
    String phrase;
    String user_id;
}