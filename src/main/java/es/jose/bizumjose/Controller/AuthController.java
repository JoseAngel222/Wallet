package es.jose.bizumjose.Controller;

import es.jose.bizumjose.Dtos.AuthResponse;
import es.jose.bizumjose.Dtos.LoginRequest;
import es.jose.bizumjose.Dtos.RegisterRequest;
import es.jose.bizumjose.Entity.User;
import es.jose.bizumjose.Security.JwtService;
import es.jose.bizumjose.Security.UserDetailsImpl;
import es.jose.bizumjose.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        User created = userService.register(req.email(), req.password(), req.fullName(), req.phoneNumber());
        String token = jwtService.generateToken(created);
        return ResponseEntity.ok(new AuthResponse(token, created.getId(), created.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        UserDetailsImpl ud = (UserDetailsImpl) auth.getPrincipal();
        // load user entity if needed:
        User u = userService.findById(ud.getId());
        String token = jwtService.generateToken(u);
        return ResponseEntity.ok(new AuthResponse(token, u.getId(), u.getEmail()));
    }
}
