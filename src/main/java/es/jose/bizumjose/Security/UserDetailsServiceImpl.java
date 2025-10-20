package es.jose.bizumjose.Security;

import es.jose.bizumjose.Entity.User;
import es.jose.bizumjose.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return toPrincipal(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Se permite login por email o teléfono
        var byEmail = userRepository.findByEmail(username);
        var byPhone = userRepository.findByPhoneNumber(username);
        User user = byEmail.or(() -> byPhone).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return toPrincipal(user);
    }

    private UserDetails toPrincipal(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
