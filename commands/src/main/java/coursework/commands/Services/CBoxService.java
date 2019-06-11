package coursework.commands.Services;

import coursework.commands.models.CBox;
import coursework.commands.models.Command;
import coursework.commands.models.User;
import coursework.commands.repositories.CBoxRepo;
import coursework.commands.repositories.CommandRepo;
import coursework.commands.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CBoxService {
    private final UserRepo userRepo;
    private final CBoxRepo repo;
    private final CommandRepo commandRepo;
    private final String NOT_FOUND_MSG = "There is no command box with such id: ";

    public CBoxService(CBoxRepo repo, CommandRepo commandRepo, UserRepo userRepo) {
        this.repo = repo;
        this.commandRepo = commandRepo;
        this.userRepo = userRepo;
    }

    private void prepare(CBox cBox, List<String> commands_ids) {
        checkCBox(cBox);
        if((commands_ids == null || commands_ids.isEmpty()) && cBox.getCommands() == null)
            throw new IllegalArgumentException("You must specify commands");
        if(commands_ids != null) {
            if (commands_ids.isEmpty()) {
                throw new IllegalArgumentException("You must specify commands");
            } else {
                List<Command> commands = commandRepo.findCommandsByIdIn(commands_ids);
                if (commands.isEmpty())
                    throw new IllegalArgumentException("There is no commands with such ids: " + commands_ids);
                else {
                    cBox.setCommands(commands);
                    cBox.setN_commands(commands.size());
                }
            }
        }
    }

    public void create(CBox cBox, List<String> commands_ids) {
        if(repo.existsCBoxByName(cBox.getName()))
            throw new IllegalArgumentException("Command box with that name already exists");
        prepare(cBox, commands_ids);
        repo.save(cBox);
    }

    public void update(String id, CBox cBox, List<String> commands_ids) {
        CBox old = repo.findCBoxById(id);
        if (old != null) {
            cBox.setCommands(old.getCommands());
            if (cBox.getName() == null || cBox.getName().equals(""))
                cBox.setName(old.getName());
            else if(!old.getName().equals(cBox.getName()) && repo.existsCBoxByName(cBox.getName()))
                throw new IllegalArgumentException("Command box with that name already exists");

            if(cBox.getDescription() == null)
                cBox.setDescription(old.getDescription());
            if(cBox.getPrice() == null)
                cBox.setPrice(old.getPrice());
            cBox.setN_commands(old.getN_commands());
            prepare(cBox, commands_ids);
            cBox.setId(id);
            repo.save(cBox);
        } else
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
    }

    public void delete(String id) {
        if(repo.existsById(id))
            repo.deleteById(id);
        else
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
    }

    public List<CBox> getAll() {
        return repo.findAll();
    }

    public CBox get(String id) {
        CBox cBox = repo.findCBoxById(id);
        if (cBox == null)
            throw new EntityNotFoundException(NOT_FOUND_MSG + id);
        else
            return cBox;
    }

    public CBox getByName(String name) {
        CBox cBox = repo.findCBoxByName(name);
        if (cBox == null)
            throw new EntityNotFoundException("There is no command box with such name: " + name);
        else
            return cBox;
    }
    private void checkCBox(CBox cBox) {
        if(cBox.getName() == null || cBox.getName().equals(""))
            throw new IllegalArgumentException("You must specify a valid command box name");
        if(cBox.getPrice() != null && cBox.getPrice() < 0)
            throw new IllegalArgumentException("Price cannot be below zero");
    }

    public boolean existBox(String id) {
        return repo.existsById(id);
    }

    public List<String> getByUser(String userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            List<String> cboxes = new ArrayList<>();
            boolean yes = true;
            for (CBox cbox:getAll()) {
                yes = true;
                List<String> coms = cbox.getCommands().stream().map(Command::getId).collect(Collectors.toList());
                for (String c:coms) {
                    if(!user.getCommands().contains(c)) {
                        yes = false;
                        break;
                    }
                }
                if (yes) cboxes.add(cbox.getId());
            }
            return cboxes;
        } else
            throw new EntityNotFoundException("There is no user with such id: " + userId);
    }
}
