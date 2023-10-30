package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
private final UserRepository userRepository;
    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    @Override
    public User getUserById(int id){
        return userRepository.getUserById(id);
    }
    @Override
    public User saveUser(User user){
       return userRepository.save(user);
    }
    @Override
    public User updateUser(int id, User user){
        return userRepository.updateUser(id,user);
    }
    @Override
    public void deleteUser(int userId){
        userRepository.deleteUser(userId);
    }
}
