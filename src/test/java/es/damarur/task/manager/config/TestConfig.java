package es.damarur.task.manager.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import es.damarur.task.manager.service.TaskManagerService;
import es.damarur.task.manager.service.TaskManagerServiceImpl;

@TestConfiguration
@ActiveProfiles({ "test" })
public class TestConfig {

	@Bean(name = "testTaskManagerService")
	public TaskManagerService taskManagerService() {
		return new TaskManagerServiceImpl();
	}
}
