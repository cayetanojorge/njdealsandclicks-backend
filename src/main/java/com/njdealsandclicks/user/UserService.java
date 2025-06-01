package com.njdealsandclicks.user;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.user.UserCreateUpdateDTO;
import com.njdealsandclicks.dto.user.UserDTO;
import com.njdealsandclicks.subscription.Subscription;
import com.njdealsandclicks.subscription.SubscriptionService;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class UserService {
    
    private static final String PREFIX_PUBLIC_ID = "user_";

    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;


    public UserService(UserRepository userRepository, SubscriptionService subscriptionService, 
                        PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, userRepository::filterAvailablePublicIds);
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setPublicId(user.getPublicId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmailVerified(user.getEmailVerified());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setDeactivatedAt(user.getDeactivatedAt());
        userDTO.setPreferredLanguage(user.getPreferredLanguage());
        userDTO.setTimezone(user.getTimezone());
        userDTO.setSubscriptionDTO(subscriptionService.getSubscriptionDTOByPublicId(user.getSubscription().getPublicId()));
        userDTO.setSubscriptionExpirationDate(user.getSubscriptionExpirationDate());
        userDTO.setRegistrationDate(user.getRegistrationDate());
        return userDTO;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(this::mapToUserDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public User getUserByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("User with publicId " + publicId + " not found"));
    }

    public UserDTO getUserDTOByPublicId(String publicId) {
        return mapToUserDTO(getUserByPublicId(publicId));
    }
    
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
    }

    @Transactional
    public UserDTO createUser(UserCreateUpdateDTO userCreateDTO) {
        User user = getUserByEmail(userCreateDTO.getEmail());
        if(user != null) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        user = new User();
        user.setPublicId(createPublicId());
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setDeactivatedAt(null);
        user.setPreferredLanguage(userCreateDTO.getPreferredLanguage());
        user.setTimezone(userCreateDTO.getTimezone());
        Subscription subscription = subscriptionService.getSubscriptionByPlanName(userCreateDTO.getSubscriptionPlanName());
        user.setSubscription(subscription);
        user.setSubscriptionExpirationDate(null);
        return mapToUserDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(String publicId, UserCreateUpdateDTO userUpdateDTO) {
        User user = getUserByPublicId(publicId);
        if(!user.getIsActive() || !user.getEmailVerified()) {
            throw new RuntimeException("User with publicId " + publicId + " is not active or has email not verified");
        }
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setPreferredLanguage(userUpdateDTO.getPreferredLanguage());
        user.setTimezone(userUpdateDTO.getTimezone());
        Subscription subscription = subscriptionService.getSubscriptionByPlanName(userUpdateDTO.getSubscriptionPlanName());
        user.setSubscription(subscription);
        user.setUpdatedAt(dateUtil.getCurrentDateTime());
        return mapToUserDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO verifyEmail(String publicId) {
        User user = getUserByPublicId(publicId);
        user.setEmailVerified(true);
        return mapToUserDTO(user);
    }

    @Transactional
    public UserDTO activateUser(String publicId) {
        User user = getUserByPublicId(publicId);
        user.setIsActive(true);
        user.setDeactivatedAt(null);
        return mapToUserDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO deactivateUser(String publicId) {
        User user = getUserByPublicId(publicId);
        user.setIsActive(false);
        user.setDeactivatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        return mapToUserDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String publicId) {
        User user = getUserByPublicId(publicId);
        userRepository.deleteById(user.getId());
    }

}
