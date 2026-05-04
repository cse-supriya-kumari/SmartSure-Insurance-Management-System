package com.smartsure.claims;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.smartsure.claims.repository.ClaimRepository;
import com.smartsure.claims.repository.ClaimDocumentRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class ClaimsServiceApplicationTests {

	@MockitoBean
	private ClaimRepository claimRepository;

	@MockitoBean
	private ClaimDocumentRepository claimDocumentRepository;

	@Test
	void contextLoads() {
	}

}
