package com.smartsure.policy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.smartsure.policy.repository.PolicyRepository;
import com.smartsure.policy.repository.PolicyTypeRepository;
import com.smartsure.policy.repository.PremiumRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class PolicyServiceApplicationTests {

	@MockitoBean
	private PolicyRepository policyRepository;

	@MockitoBean
	private PolicyTypeRepository policyTypeRepository;

	@MockitoBean
	private PremiumRepository premiumRepository;

	@Test
	void contextLoads() {
	}

}
