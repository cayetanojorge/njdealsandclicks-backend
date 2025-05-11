package com.njdealsandclicks.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.user.UserCreateUpdateDTO;
import com.njdealsandclicks.dto.user.UserDTO;


@RestController
@RequestMapping("/api/public/user")
public class UserPublicController {
    
    private final UserService userService;

    public UserPublicController(UserService userService) {
        this.userService = userService;
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
    public UserDTO createUser(@RequestBody UserCreateUpdateDTO userCreateDTO) {
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
}
