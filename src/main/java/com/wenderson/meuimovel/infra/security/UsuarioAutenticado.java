package com.wenderson.meuimovel.infra.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

public class UsuarioAutenticado {

    public UUID obterUsuarioId(){

        //Recupera autenticacao atual do Spring Security.
        String subject = getSubject();

        if ( subject == null || subject.isBlank()){
            throw new BadCredentialsException(
                    "JWT sem identificador de usuário"
            );
        }

        try  {
            return UUID.fromString(subject);
        }catch (IllegalArgumentException exception){
            throw new BadCredentialsException(
                    "identificador de usuário invalido no JWT",
                    exception
            );
        }
    }

    private static @Nullable String getSubject() {
        var autenticacao = SecurityContextHolder
                .getContext()
                .getAuthentication();

        //Exibe que a autenticacao seja baseada em JWT
        if (!(autenticacao instanceof JwtAuthenticationToken jwt)
                || !autenticacao.isAuthenticated()
        ) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Usuário não autenticado"
            );
        }

        // O Claim "Sub" identifica o usuário autenticado.
        String subject = jwt.getToken()
                .getSubject();
        return subject;
    }
}
