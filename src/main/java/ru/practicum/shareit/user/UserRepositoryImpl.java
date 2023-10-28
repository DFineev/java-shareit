package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Component
public class UserRepositoryImpl implements UserRepository{

    private final Map<Integer,User> users = new HashMap<>();
    public int nextId = 1;
    @Override
    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        user.setId(nextId++);
        users.put(user.getId(),user);
        return user;
    }
    @Override
    public User getUserById(int id){
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException();
        }
        return users.get(id);
    }
}
