package antifraud.api.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService{
    UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User addUser(User user){
        Iterable<User> users = repository.findAll();
        for (User user1 : users){
            if(user1.getUsername().equals(user.getUsername())){
                return null;
            }
        }
        return repository.save(user);
    }

    public List getAllUsers(){
        List<Map<String, Object>> list = new ArrayList<>();
        for (User user : repository.findAll()){
            list.add(Map.of("id",user.getId()
            ,"name", user.getName(), "username", user.getUsername(), "role",user.getRole().substring(5)));
        }

        return list;
    }

    public User findUser(String username){
        Iterable<User> users = repository.findAll();
        for (User user1 : users){
            if(user1.getUsername().equals(username)){
                return user1;
            }
        }
        return null;
    }

    public User updateUser(UserRole userRole){
        User user = findUser(userRole.getUsername());
        if(user != null){
            user.setRole("ROLE_"+userRole.getRole().trim());
            return repository.save(user);
        }else return null;
    }
    public boolean deleteUser(String username){
        User user = findUser(username);
        if(user != null){
            repository.delete(user);
            return true;
        }
        else return false;
    }

    public void clear(){
        repository.deleteAll();
    }


}
