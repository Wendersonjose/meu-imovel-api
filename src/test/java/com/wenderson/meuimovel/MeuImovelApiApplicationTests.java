
package com.wenderson.meuimovel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:postgresql://localhost:5432/meu_imovel_dev",
		"spring.datasource.username=${DB_USERNAME}",
		"spring.datasource.password=${DB_PASSWORD}",
		"spring.flyway.url=jdbc:postgresql://localhost:5432/meu_imovel_dev",
		"spring.flyway.user=${DB_USERNAME}",
		"spring.flyway.password=${DB_PASSWORD}",
		"SUPABASE_URL=https://supabase.invalid",
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=https://supabase.invalid/auth/v1",
		"spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://supabase.invalid/auth/v1/.well-known/jwks.json"
})
@ActiveProfiles("dev")
class MeuImovelApiApplicationTests {

	@Test
	void contextLoads() {
	}
}
