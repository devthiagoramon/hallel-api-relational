package br.hallel.relational.api.app;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AppApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(AppApplicationTests.class);
	@Autowired
	private MinistryRepository ministryRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void listMemberMinistry(){
		Ministry ministry = ministryRepository.findById(UUID.fromString("8675330f-9c78-4c6d-9230-b046b4097392"))
				.orElse(null);

        Assertions.assertNotNull(ministry);
		Assertions.assertNotNull(ministry.getId());

	}

}
