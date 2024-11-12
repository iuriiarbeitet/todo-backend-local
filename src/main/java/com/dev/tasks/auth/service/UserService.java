package com.dev.tasks.auth.service;

import com.dev.tasks.auth.entity.Activity;
import com.dev.tasks.auth.entity.Role;
import com.dev.tasks.auth.entity.User;
import com.dev.tasks.auth.repository.ActivityRepository;
import com.dev.tasks.auth.repository.RoleRepository;
import com.dev.tasks.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    public static final String DEFAULT_ROLE = "USER";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivityRepository activityRepository;


    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.activityRepository = activityRepository;
    }

    public void register(User user, Activity activity) {
        userRepository.save(user);
        activityRepository.save(activity);
    }

    public boolean userExists(String username, String email) {

        return userRepository.existsByUsername(username) || userRepository.existsByEmail(email);
    }

    public Optional<Role> findByName(String role) {
        return roleRepository.findByName(role);
    }

//    public Activity saveActivity(Activity activity) {
//        return activityRepository.save(activity);
//    }

    public Optional<Activity> findActivityByUserId(long id) {
        return activityRepository.findByUserId(id);
    }

    public Optional<Activity> findActivityByUuid(String uuid) {
        return activityRepository.findByUuid(uuid);
    }

    public int activate(String uuid) {
        return activityRepository.changeActivated(uuid, true);
    }

//    public int deactivate(String uuid) {
//        return activityRepository.changeActivated(uuid, false);
//    }

    public int updatePassword(String password, String email) {
        return userRepository.updatePassword(password, email);
    }

}
