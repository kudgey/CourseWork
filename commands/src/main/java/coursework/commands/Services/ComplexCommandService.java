package coursework.commands.Services;

import coursework.commands.models.Argument;
import coursework.commands.models.CBox;
import coursework.commands.models.Command;
import coursework.commands.models.ComplexCommand;
import coursework.commands.repositories.ArgumentRepo;
import coursework.commands.repositories.CommandRepo;
import coursework.commands.repositories.ComplexCommandRepo;
import coursework.commands.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.print.attribute.HashAttributeSet;
import java.util.*;

@Service
public class ComplexCommandService {
    private final ComplexCommandRepo repo;
    private final CommandRepo commandRepo;
    private final ArgumentRepo argumentRepo;
    final
    UserRepo userRepo;
    private final String NOT_FOUND_MSG = "There is no complex command box with such id: ";

    public ComplexCommandService(ComplexCommandRepo repo, CommandRepo commandRepo, ArgumentRepo argumentRepo, UserRepo userRepo) {
        this.repo = repo;
        this.commandRepo = commandRepo;
        this.argumentRepo = argumentRepo;
        this.userRepo = userRepo;
    }

    private void prepare(ComplexCommand complexCommand, List<String> commands_ids, HashMap<String, Object> args) {
        checkCCommand(complexCommand);
        if((commands_ids == null || commands_ids.isEmpty()) && complexCommand.getCommands() == null)
            throw new IllegalArgumentException("You must specify commands");
        if(commands_ids != null) {
            if (commands_ids.isEmpty()) {
                throw new IllegalArgumentException("You must specify commands");
            } else {
                List<Command> commands = new ArrayList<>();
                for (String cid:
                     commands_ids) {
                    Command c = commandRepo.findCommandById(cid);
                    if (c == null)
                        throw new IllegalArgumentException("There is no commands with such id: " + cid);
                    else {
                        commands.add(c);
                    }
                }
                complexCommand.setCommands(commands);
            }
        }
        if (args != null && !args.isEmpty()){
//            HashMap<Argument, Object> arguments = new HashMap<>();
//            for (Map.Entry<String, Object> a:
//                 args.entrySet()) {
//                Argument arg = argumentRepo.findArgumentById(a.getKey());
//                if (arg != null)
//                    arguments.put(arg, a.getValue());
//            }
            complexCommand.setArgs(args);
        }
    }

    public void create(ComplexCommand complexCommand, List<String> commands_ids, HashMap<String, Object> args) {
        if(repo.existsComplexCommandByName(complexCommand.getName()))
            throw new IllegalArgumentException("Complex command with that name already exists");
        if(repo.existsComplexCommandByPhrase(complexCommand.getPhrase()) || commandRepo.existsCommandsByPhrase(complexCommand.getPhrase()))
            throw new IllegalArgumentException("Complex command with that phrase already exists");
        prepare(complexCommand, commands_ids, args);
        repo.save(complexCommand);
    }

    public void update(String id, ComplexCommand complexCommand, List<String> commands_ids, HashMap<String, Object> args) {
        ComplexCommand old = repo.findComplexCommandById(id);
        if (old != null) {
            complexCommand.setCommands(old.getCommands());
            complexCommand.setArgs(old.getArgs());
            if (complexCommand.getName() == null || complexCommand.getName().equals(""))
                complexCommand.setName(old.getName());
            else if(!old.getName().equals(complexCommand.getName()) && repo.existsComplexCommandByName(complexCommand.getName()))
                throw new IllegalArgumentException("Complex command with that name already exists");

            if(complexCommand.getDescription() == null)
                complexCommand.setDescription(old.getDescription());
            if(complexCommand.getPhrase() == null || complexCommand.getPhrase().equals(""))
                complexCommand.setPhrase(old.getPhrase());
            else if(!old.getPhrase().equals(complexCommand.getPhrase()) && (repo.existsComplexCommandByPhrase(complexCommand.getPhrase()) || commandRepo.existsCommandsByPhrase(complexCommand.getPhrase())))
                throw new IllegalArgumentException("Сommand with that phrase already exists");
            if(complexCommand.getArgs() == null || complexCommand.getArgs().isEmpty())
                complexCommand.setArgs(old.getArgs());

            prepare(complexCommand, commands_ids, args);
            complexCommand.setId(id);
            repo.save(complexCommand);
        } else
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
    }

    public void delete(String id) {
        if(repo.existsById(id))
            repo.deleteById(id);
        else
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
    }

    public List<ComplexCommand> getAll() {
        return repo.findAll();
    }

    public ComplexCommand get(String id) {
        ComplexCommand complexCommand = repo.findComplexCommandById(id);
        if (complexCommand == null)
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
        else
            return complexCommand;
    }
    private void checkCCommand(ComplexCommand complexCommand) {
        if(complexCommand.getName() == null || complexCommand.getName().equals(""))
            throw new IllegalArgumentException("You must specify a valid complex command name");
        if(complexCommand.getPhrase() == null || complexCommand.getPhrase().equals(""))
            throw new IllegalArgumentException("You must specify phrase");
    }

    public ComplexCommand getByPhrase(String phrase, String userId) {
        ComplexCommand command = repo.findComplexCommandByPhrase(phrase);
        if (command == null)
            throw new IllegalArgumentException("There is no command with such phrase");
        else {
            for (Command cmd:command.getCommands()) {
                if (!userRepo.existsUserByIdAndCommandsContains(userId, cmd.getId()))
                    throw new IllegalArgumentException("You don't have this command");
            }
            return command;
        }
    }

}
