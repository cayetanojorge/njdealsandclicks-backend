package com.njdealsandclicks.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.user.UserCreateUpdateDTO;
import com.njdealsandclicks.dto.user.UserDTO;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{publicId}")
    public UserDTO getUserByPublicId(@PathVariable String publicId) {
        return userService.getUserDTOByPublicId(publicId);
    }

    @GetMapping("/email")
    public User getUserByEmail(@RequestBody String email) {
        return userService.getUserByEmail(email);
    }
    
    @PostMapping("/create")
    public UserDTO creatUser(@RequestBody UserCreateUpdateDTO userCreateDTO) {
        return userService.createUser(userCreateDTO);
    }

    @PutMapping("/{publicId}")
    public UserDTO updateUser(@PathVariable String publicId, @RequestBody UserCreateUpdateDTO userUpdateDTO) {
        return userService.updateUser(publicId, userUpdateDTO);
    }
    
    @PutMapping("/{publicId}/verify-email")
    public UserDTO verifyEmail(@PathVariable String publicId) {
        return userService.verifyEmail(publicId);
    }

    @PutMapping("/{publicId}/activate")
    public UserDTO activateUser(@PathVariable String publicId) {
        return userService.activateUser(publicId);
    }

    @PutMapping("/{publicId}/deactivate")
    public UserDTO deactivateUser(@PathVariable String publicId) {
        return userService.deactivateUser(publicId);
    }

    @PatchMapping("/{publicId}/email-frequency")
    public UserDTO updateEmailFrequency(@PathVariable String publicId, @RequestBody String emailFrequency) {
        return userService.updateEmailFrequency(publicId, emailFrequency);
    }

    @DeleteMapping("/delete/{publicId}")
    public void deleteUser(@PathVariable String publicId) {
        userService.deleteUser(publicId);
    }
}
