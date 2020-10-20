package es.damarur.task.manager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import es.damarur.task.manager.model.TaskEntity;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskEntityRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private TaskEntityRepository taskRepository;

	@Before
	public void init() {
		TaskEntity task = new TaskEntity();
		task.setTitle("test");
		entityManager.persist(task);
		entityManager.flush();
	}

	@Test
	public void taskRepositoryTests() {
		List<TaskEntity> tasks = taskRepository.findAll();
		assertThat(tasks).isNotEmpty();
		int updated = taskRepository.updateFinishedTask(true, new Date(), tasks.get(0).getId());
		assertThat(updated).isPositive();
		int count = taskRepository.countTaskEntityByIdAndFinished(tasks.get(0).getId(), true);
		assertThat(count).isPositive();
	}
}
