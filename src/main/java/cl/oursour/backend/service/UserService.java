package cl.oursour.backend.service;

import cl.oursour.backend.exception.ApiException;
import cl.oursour.backend.model.User;
import cl.oursour.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
 
@Service
public class UserService {
 
    private final UserRepository userRepository;
 
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
 
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }
}

