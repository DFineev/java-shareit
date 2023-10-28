package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
@Component
public class UserRepositoryImpl implements UserRepository{

    private final List<User> users = new ArrayList<>();
    public int nextId = 1;
    @Override
    public List<User> findAll(){
        return users;
    }

    @Override
    public User save(User user) {
        user.setId(nextId++);
        users.add(user);
        return user;
    }
    @Override
    public User getUserById(int id){
       return users.get(id);

    }
}
