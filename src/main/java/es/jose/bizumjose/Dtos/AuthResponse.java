package es.jose.bizumjose.Dtos;

public record AuthResponse(String token, Long userId, String email) {}
