package coursework.admin;

import coursework.admin.models.User;
import coursework.admin.repositories.RoleRepo;
import coursework.admin.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MongoUserDetailsService implements UserDetailsService {
    private final UserRepo repository;
    final
    RoleRepo roleRepo;

    public MongoUserDetailsService(UserRepo repository, RoleRepo roleRepo) {
        this.repository = repository;
        this.roleRepo = roleRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<SimpleGrantedAuthority> auths =
                user.getRole().getName().equals("Admin") ?
                        roleRepo.findAll().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList()) :
                        Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), auths);
    }
}