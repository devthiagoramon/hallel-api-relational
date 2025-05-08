package br.hallel.relational.api.app.user.dto;


public record UserLoginDTO(
        String name,
        String email,
        String password,
        String token) {

}
