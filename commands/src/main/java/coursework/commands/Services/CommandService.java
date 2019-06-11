package coursework.commands.Services;

import coursework.commands.models.Argument;
import coursework.commands.models.Command;
import coursework.commands.models.Language;
import coursework.commands.models.User;
import coursework.commands.repositories.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandService {
    private final CommandRepo repo;
    private final ArgumentRepo argumentRepo;
    private final LanguageRepo languageRepo;
    private final UserRepo userRepo;
    private final CBoxRepo cBoxRepo;
    private final ComplexCommandRepo complexCommandRepo;
    final
    AdminClient client;
    private final String NOT_FOUND_MSG = "There is no command with such id: ";

    public CommandService(CommandRepo repo, ArgumentRepo argumentRepo, LanguageRepo languageRepo, UserRepo userRepo, CBoxRepo cBoxRepo, ComplexCommandRepo complexCommandRepo, AdminClient client) {
        this.repo = repo;
        this.argumentRepo = argumentRepo;
        this.languageRepo = languageRepo;
        this.userRepo = userRepo;
        this.cBoxRepo = cBoxRepo;
        this.complexCommandRepo = complexCommandRepo;
        this.client = client;
    }

    private void prepare(Command command, List<String> arg_ids, String lang_id, String parent_id, String user_id) {
        checkCommand(command);
        if(arg_ids != null) {
             if (arg_ids.isEmpty()) {
                 command.setArguments(null);
             } else {
                 List<Argument> args = argumentRepo.findArgumentsByIdIn(arg_ids);
                 if (args.isEmpty())
                     throw new IllegalArgumentException("There is no arguments with such ids: " + arg_ids);
                 else
                     command.setArguments(args);
             }
        }

        if((lang_id == null || lang_id.equals("")) && command.getLang() == null)
            throw new IllegalArgumentException("You must specify a language id");
        if(lang_id != null) {
            Language lang = languageRepo.findLanguageById(lang_id);
            if (lang != null)
                command.setLang(lang);
            else
                throw new IllegalArgumentException("There is no language with such id: " + lang_id);
        }

        if(parent_id != null) {
            if (parent_id.equals(""))
                command.setParent(null);
            else {
                Command parent = repo.findCommandById(parent_id);
                if (parent != null)
                    command.setParent(parent);
                else
                    throw new IllegalArgumentException("There is no command with such id: " + parent_id);
            }
        }

        if(user_id != null) {
            if (user_id.equals(""))
                command.setUser(null);
            else {
                User user = userRepo.findUserById(user_id);
                if (user != null)
                    command.setUser(user);
                else
                    throw new IllegalArgumentException("There is no user with such id: " + user_id);
            }
        }
    }

    public void create(Command command, List<String> arg_ids, String lang_id, String parent_id, String user_id) {
        if(repo.existsCommandByName(command.getName()))
            throw new IllegalArgumentException("Command with that name already exists");
        if(repo.existsCommandsByPhrase(command.getPhrase()) || complexCommandRepo.existsComplexCommandByPhrase(command.getPhrase()))
            throw new IllegalArgumentException("Command with that phrase already exists");
        prepare(command, arg_ids, lang_id, parent_id, user_id);
        repo.save(command);
        if (command.getParent() != null) {
            User user = userRepo.findUserById(user_id);
            List<String> coms = user.getCommands();
            coms.add(command.getId());
            user.setCommands(coms);
            userRepo.save(user);
        }
    }

    public void update(String id, Command command, List<String> arg_ids, String lang_id, String parent_id, String user_id) {
        Command old = repo.findCommandById(id);
        if (old != null) {
            command.setLang(old.getLang());
            command.setParent(old.getParent());
            command.setUser(old.getUser());
            command.setArguments(old.getArguments());
            if (command.getName() == null || command.getName().equals(""))
                command.setName(old.getName());
            else if(!old.getName().equals(command.getName()) && repo.existsCommandByName(command.getName()))
                throw new IllegalArgumentException("Command with that name already exists");

            if(command.getDescription() == null)
                command.setDescription(old.getDescription());
            if(command.getCode() == null)
                command.setCode(old.getCode());
            if(command.getPrice() == null)
                command.setPrice(old.getPrice());
            if(command.getPhrase() == null || command.getPhrase().equals(""))
                command.setPhrase(old.getPhrase());
            else if(!old.getPhrase().equals(command.getPhrase()) && (repo.existsCommandsByPhrase(command.getPhrase()) || complexCommandRepo.existsComplexCommandByPhrase(command.getPhrase())))
                throw new IllegalArgumentException("Command with that phrase already exists");

            prepare(command, arg_ids, lang_id, parent_id, user_id);
            command.setId(id);
            repo.save(command);
        } else
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
    }

    public void delete(String id) {
        if(repo.existsCommandById(id)) {
            if (cBoxRepo.existsCBoxesByCommandsContains(repo.findCommandById(id)))
                throw new IllegalArgumentException("There are command boxes that contain this command. You cannot delete it");
            if (complexCommandRepo.existsComplexCommandsByCommandsContains(repo.findCommandById(id)))
                throw new IllegalArgumentException("There are complex commands that contain this command. You cannot delete it");
            if(userRepo.existsUserByCommandsContains(id))
                throw new IllegalArgumentException("There are users that contain this command. You cannot delete it");
            repo.deleteById(id);
        }
        else
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
    }

    public List<Command> getAll() {
        return repo.findAllByOpenIsTrue();
    }

    public List<Command> getAllByUser(String userId) {
        if(userRepo.existsById(userId)) {
            List<String> commands = userRepo.findUserById(userId).getCommands();
            return commands.stream().map(this::get).collect(Collectors.toList());
        } else
            throw new EntityNotFoundException("There is no user with such id: " + userId);
    }

    public Command get(String id) {
        Command command = repo.findCommandById(id);
        if (command == null)
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
        else
            return command;
    }

    private void checkCommand(Command command) {
        if(command.getName() == null || command.getName().equals(""))
            throw new IllegalArgumentException("You must specify a valid command name");
        if(command.getPhrase() == null || command.getPhrase().equals(""))
            throw new IllegalArgumentException("You need to specify phrase");
        if(command.getPrice() != null && command.getPrice() < 0)
            throw new IllegalArgumentException("Price cannot be below zero");
        if(command.getCode() == null || command.getCode().equals(""))
            throw new IllegalArgumentException("You need to specify code");
    }

    public Command getByPhrase(String phrase, String user_id) {
        Command c = repo.findByPhrase(phrase);
        if (c == null)
            return null;
        if (userRepo.existsUserByIdAndCommandsContains(user_id, c.getId()))
            return c;
        else
            throw new IllegalArgumentException("You dont have this command");
    }

    public boolean existCommands(String[] ids) {
        if (ids == null || ids.length == 0)
            return false;
        for (String id : ids) {
            if(!repo.existsCommandById(id) && !complexCommandRepo.existsById(id))
                return false;
        }
        return true;
    }
}
