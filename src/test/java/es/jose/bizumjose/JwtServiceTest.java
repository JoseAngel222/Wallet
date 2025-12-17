package es.jose.bizumjose;

import es.jose.bizumjose.Entity.User;
import es.jose.bizumjose.Security.JwtService;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    JwtService jwtService =
            new JwtService("alfinalLasCosasSiempreFuncionan555623232EEE", 1000);

    @Test
    void generate_and_validate_token() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .roles(Set.of("USER"))
                .build();

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token));
        assertEquals(1L, jwtService.extractUserId(token));
    }
}

