package es.jose.bizumjose.Service;

import es.jose.bizumjose.Entity.User;

public interface UserService {
    User register(String email, String rawPassword, String fullName, String phoneNumber);
    User findByEmail(String email);
    User findById(Long id);
}
