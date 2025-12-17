package es.jose.bizumjose.Security;

import es.jose.bizumjose.Entity.User;
import es.jose.bizumjose.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // usado por Spring Security (login)
    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(u);
    }

    // helper para cargar por id (desde JwtFilter)
    public UserDetailsImpl loadUserById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(u);
    }
}
