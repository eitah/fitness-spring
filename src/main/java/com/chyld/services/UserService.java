package com.chyld.services;

import com.chyld.entities.Exercise;
import com.chyld.entities.User;
import com.chyld.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private IUserRepository repository;

    @Autowired
    public void setRepository(IUserRepository repository) {
        this.repository = repository;
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public User findUserById(Integer id) {
        return repository.findOne(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.repository.findByUsername(username);
    }

    public List<User> getAllUsers()
    {
        return (List<User>) this.repository.findAll();
    }

    public Exercise getExerciseByUserIdandExerciseId(int id, int exerciseID) {
        User u = this.findUserById(id);
        List<Exercise> exercises = u.getExercises();

        for (Exercise e : exercises ) {
            if (e.getId() == exerciseID) {
                return e;
            }
        }

        return (new Exercise());
    }
}
