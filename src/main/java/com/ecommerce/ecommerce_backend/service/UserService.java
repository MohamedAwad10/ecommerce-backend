package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dao.LocalUserDao;
import com.ecommerce.ecommerce_backend.dto.RegistrationBody;
import com.ecommerce.ecommerce_backend.exception.UserAlreadyExistException;
import com.ecommerce.ecommerce_backend.model.LocalUser;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private LocalUserDao userDao;

    public UserService(LocalUserDao localUserDao) {
        this.userDao = localUserDao;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistException{

        if(userDao.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()
                || userDao.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()){
            throw new UserAlreadyExistException();
        }

        LocalUser user = new LocalUser();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        // TODO: encrypt password
        user.setPassword(registrationBody.getPassword());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        return userDao.save(user);
    }
}
