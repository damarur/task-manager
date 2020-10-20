package es.damarur.task.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import es.damarur.task.manager.config.TestConfig;
import es.damarur.task.manager.dto.TaskDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;
import es.damarur.task.manager.model.TaskEntity;
import es.damarur.task.manager.repository.TaskEntityRepository;

@RunWith(SpringRunner.class)
@Import(TestConfig.class)
public class TaskManagerServiceTest {

	private static final String TASK_TITLE_TEST = "task";

	@MockBean
	private TaskEntityRepository taskEntityRepository;

	@Autowired
	@Qualifier(value = "testTaskManagerService")
	private TaskManagerService taskManagerService;

	@Before
	public void init() {
		TaskEntity task = new TaskEntity();
		task.setId(1);
		task.setTitle(TASK_TITLE_TEST);
		Optional<TaskEntity> opTask = Optional.of(task);
		when(taskEntityRepository.findById(task.getId())).thenReturn(opTask);
		when(taskEntityRepository.save(any(TaskEntity.class))).thenReturn(task);
		when(taskEntityRepository.existsById(1)).thenReturn(true);
		when(taskEntityRepository.existsById(2)).thenReturn(false);
		when(taskEntityRepository.countTaskEntityByIdAndFinished(1, true)).thenReturn(1);
		when(taskEntityRepository.countTaskEntityByIdAndFinished(2, true)).thenReturn(0);
		when(taskEntityRepository.updateFinishedTask(eq(true), any(Date.class), eq(1))).thenReturn(1);
		when(taskEntityRepository.updateFinishedTask(eq(true), any(Date.class), eq(2))).thenReturn(0);
		List<TaskEntity> tasks = new ArrayList<>(1);
		tasks.add(task);
		Page<TaskEntity> pagedResponse = new PageImpl(tasks);
		Page<TaskEntity> emptyResponse = new PageImpl(new ArrayList<>());
		Pageable pageZero = PageRequest.of(0, 10);
		Pageable pageOne = PageRequest.of(1, 10);
		when(taskEntityRepository.findAll(any(Specification.class), eq(pageZero))).thenReturn(pagedResponse);
		when(taskEntityRepository.findAll(any(Specification.class), eq(pageOne))).thenReturn(emptyResponse);
	}

	@Test
	public void createTaskTest() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setTitle(TASK_TITLE_TEST);
		TaskDTO created = taskManagerService.createTask(taskDTO);
		assertNotNull(created.getId());
	}

	@Test
	public void updateTaskTest() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setId(1);
		taskDTO.setTitle(TASK_TITLE_TEST + " modified");
		TaskDTO updated;
		try {
			updated = taskManagerService.updateTask(taskDTO);
			assertThat(updated.getId()).isEqualTo(1);
			taskDTO.setId(2);
			updated = taskManagerService.updateTask(taskDTO);
		} catch (Exception e) {
			assertThat(e).isInstanceOf(TaskNotFoundException.class);
		}
	}

	@Test
	public void deleteTaskTest() {
		try {
			boolean result = taskManagerService.deleteTask(1);
			assertThat(result).isTrue();
			result = taskManagerService.deleteTask(2);
		} catch (Exception e) {
			assertThat(e).isInstanceOf(TaskNotFoundException.class);
		}
	}

	@Test
	public void finishTaskTest() {
		try {
			boolean result = taskManagerService.finishTask(1);
			assertThat(result).isTrue();
			result = taskManagerService.finishTask(2);
			assertThat(result).isFalse();
			// Force exception
			result = taskManagerService.finishTask(1);
		} catch (Exception e) {
			assertThat(e).isInstanceOf(BusinessException.class);
		}
	}

	@Test
	public void searchTasksTest() {
		Date now = new Date();
		Pageable pageZero = PageRequest.of(0, 10);
		List<TaskDTO> results = taskManagerService.searchTasks(TASK_TITLE_TEST, true, now, now, pageZero);
		assertThat(results).isNotEmpty();
		Pageable pageOne = PageRequest.of(1, 10);
		results = taskManagerService.searchTasks(TASK_TITLE_TEST, true, now, now, pageOne);
		assertThat(results).isEmpty();
	}
}
