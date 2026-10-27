
package com.wenderson.meuimovel.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // API REST autenticada por Bearer Token.
                // Nao utiliza sessao HTTP para autenticacao.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Desativa CSRF para a API stateless.
                // A autenticacao utilizara o header Authorization.
                .csrf(AbstractHttpConfigurer::disable)

                // Desativa os mecanismos tradicionais de login.
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Exige autenticacao em todos os endpoints.
                // Nenhuma rota sera publica por padrao.
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )

                // Prepara a validacao de tokens JWT.
                // O emissor e as chaves serao configurados
                // na proxima etapa.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
