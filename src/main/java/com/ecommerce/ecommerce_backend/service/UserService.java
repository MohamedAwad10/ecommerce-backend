package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dao.LocalUserDao;
import com.ecommerce.ecommerce_backend.dto.LoginBody;
import com.ecommerce.ecommerce_backend.dto.LoginResponse;
import com.ecommerce.ecommerce_backend.dto.RegistrationBody;
import com.ecommerce.ecommerce_backend.exception.UserAlreadyExistException;
import com.ecommerce.ecommerce_backend.model.LocalUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private LocalUserDao userDao;

    private EncryptionService encryptionService;

    private JWTService jwtService;

    public UserService(LocalUserDao localUserDao, EncryptionService encryptionService, JWTService jwtService) {
        this.userDao = localUserDao;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistException{

        if(userDao.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()
                || userDao.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()){
            throw new UserAlreadyExistException();
        }

        LocalUser user = new LocalUser();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        return userDao.save(user);
    }

    public String loginUser(LoginBody loginBody){

        Optional<LocalUser> optionalUser = userDao.findByUsernameIgnoreCase(loginBody.getUsername());
        if(optionalUser.isPresent()){
            LocalUser user = optionalUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                return jwtService.generateToken(user);
            }
        }

        return null;
    }
}
