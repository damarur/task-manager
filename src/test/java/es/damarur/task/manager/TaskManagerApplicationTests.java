package es.damarur.task.manager;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import es.damarur.task.manager.controller.HomeController;
import es.damarur.task.manager.service.TaskManagerService;

@SpringBootTest(classes = TaskManagerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class TaskManagerApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private HomeController controller;

	@Autowired
	private TaskManagerService service;

	@Test
	void contextLoads() {
		assertNotNull(controller);
		assertNotNull(service);
	}

}
