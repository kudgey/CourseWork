package coursework.manager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class MainController {
    private final Producer producer;
    private final CommandsClient client;

    public MainController(CommandsClient client, Producer producer) {
        this.client = client;
        this.producer = producer;
    }

    @PostMapping("/command")
    public ResponseEntity<Object> getCommand(@RequestBody PhraseUser phraseUser) throws IOException {
        try {
            String text = phraseUser.getPhrase();
            System.out.println(text);
            String name = "";
            PythonCommand pythonCommand = new PythonCommand();
            if (text.contains(".")) {
                String[] words = text.split("\\.");
                String phrase = words[0].trim().toLowerCase();
                String args = words[1].trim().toLowerCase();

                phraseUser.setPhrase(phrase);
                ResponseEntity<Object> resp = client.getCommandByPhrase(phraseUser);
                CommandExecuteJson commandExecuteJson = new ObjectMapper().convertValue(resp.getBody(), CommandExecuteJson.class);
                assert commandExecuteJson != null;
                name = commandExecuteJson.getName();
                pythonCommand.setCodes(commandExecuteJson.getCodes());
                HashMap<String, Object> arguments = new HashMap<>();
                for (Argument arg :
                        commandExecuteJson.getArgs()) {
                    Pattern compile = Pattern.compile(
                            arg.getRegex_pattern(),
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS);
                    Matcher m = compile.matcher(args);
                    if (m.find()) {
                        String found;
                        if (arg.getType().startsWith("array")) {
                            String type = arg.getType().split(" ")[1];
                            List<Object> ar = new ArrayList<>();
                            for (int i = 1; i <= m.groupCount(); i++) {
                                found = m.group(i);
                                if (type.equals("int"))
                                    ar.add(Integer.valueOf(found));
                                else
                                    ar.add(found);
                            }
                            arguments.put(arg.getName(), ar);
                        } else {
                            found = m.group(1);
                            if (arg.getType().equals("int"))
                                arguments.put(arg.getName(), Integer.valueOf(found));
                            else if (arg.getType().startsWith("enum")) {
                                String[] chs = arg.getType().split(" ")[1].split(",");
                                if (Arrays.asList(chs).contains(found))
                                    arguments.put(arg.getName(), found);
                                else
                                    return new ResponseEntity<>("There is no such choice for argument " + arg.getName(), HttpStatus.BAD_REQUEST);

                            } else
                                arguments.put(arg.getName(), found);
                        }
                    }

                }
                pythonCommand.setArgs(arguments);
            } else {
                text = text.toLowerCase().trim();
                phraseUser.setPhrase(text);
                ResponseEntity<Object> resp = client.getCommandByPhrase(phraseUser);
                CommandExecuteJson commandExecuteJson = new ObjectMapper().convertValue(resp.getBody(), CommandExecuteJson.class);
                assert commandExecuteJson != null;
                name = commandExecuteJson.getName();
                pythonCommand.setCodes(commandExecuteJson.getCodes());
                if (commandExecuteJson.getReadyArgs() != null && !commandExecuteJson.getReadyArgs().isEmpty())
                    pythonCommand.setArgs(commandExecuteJson.getReadyArgs());
//                if (commandExecuteJson.getArgs() == null || commandExecuteJson.getArgs().isEmpty()) {
//                } else
//                    return new ResponseEntity<>("This command needs arguments", HttpStatus.BAD_REQUEST);
            }
            producer.sendMessage(pythonCommand.getArgs(), name, phraseUser.getUser_id());
            return new ResponseEntity<>(pythonCommand, HttpStatus.OK);
        }
        catch (FeignException ex){
            return new ResponseEntity<>("There is no such command", HttpStatus.BAD_REQUEST);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return new ResponseEntity<>("There is no '.' in phrase. Cannot find arguments", HttpStatus.BAD_REQUEST);
        }
        catch (IllegalStateException ex){
            return new ResponseEntity<>("Check your arguments. There is a problem with them", HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CommandExecuteJson {
    List<Argument> args;
    String[] codes;
    String name;
    HashMap<String, Object> readyArgs;
}



@Data
@AllArgsConstructor
@NoArgsConstructor
class Argument {
    String name;
    String regex_pattern;
    String type;

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class PythonCommand {
    HashMap<String, Object> args;
    String[] codes;
}
