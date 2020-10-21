package es.damarur.task.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import es.damarur.task.manager.dto.TaskDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;
import es.damarur.task.manager.model.TaskEntity;
import es.damarur.task.manager.repository.TaskEntityRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class TaskManagerServiceTest {

	private static final String TASK_TITLE_TEST = "task";
	private TaskEntity mockedTask;

	@Mock
	private TaskEntityRepository taskEntityRepository;

	@InjectMocks
	private TaskManagerServiceImpl taskManagerService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockedTask = new TaskEntity();
		mockedTask.setTitle(TASK_TITLE_TEST);
		when(taskEntityRepository.save(any(TaskEntity.class))).then(new Answer<TaskEntity>() {
			int sequence = 1;

			@Override
			public TaskEntity answer(InvocationOnMock invocation) throws Throwable {
				TaskEntity task = (TaskEntity) invocation.getArgument(0);
				task.setId(sequence++);
				mockedTask = task;
				return task;
			}
		});
		Optional<TaskEntity> opTask = Optional.of(mockedTask);
		when(taskEntityRepository.findById(any(Integer.class))).thenReturn(opTask);
		when(taskEntityRepository.findById(10)).thenReturn(Optional.empty());
		when(taskEntityRepository.existsById(1)).thenReturn(true);
		when(taskEntityRepository.existsById(2)).thenReturn(false);
		when(taskEntityRepository.countTaskEntityByIdAndFinished(1, true)).thenReturn(0);
		when(taskEntityRepository.countTaskEntityByIdAndFinished(2, true)).thenReturn(1);
		when(taskEntityRepository.updateFinishedTask(eq(true), any(Date.class), eq(1))).thenReturn(1);
		when(taskEntityRepository.updateFinishedTask(eq(true), any(Date.class), eq(2))).thenReturn(0);
		List<TaskEntity> tasks = new ArrayList<>(1);
		tasks.add(mockedTask);
		Page<TaskEntity> pagedResponse = new PageImpl(tasks);
		Page<TaskEntity> emptyResponse = new PageImpl(new ArrayList<>());
		Pageable pageZero = PageRequest.of(0, 10);
		Pageable pageOne = PageRequest.of(1, 10);
		when(taskEntityRepository.findAll(any(Specification.class), eq(pageZero))).thenReturn(pagedResponse);
		when(taskEntityRepository.findAll(any(Specification.class), eq(pageOne))).thenReturn(emptyResponse);
	}

	@Test
	public void givenNewTask_whenCreateTask_thenSucceed() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setTitle(TASK_TITLE_TEST);
		TaskDTO created = taskManagerService.createTask(taskDTO);
		assertNotNull(created.getId());
		verify(taskEntityRepository).save(mockedTask);
	}

	@Test
	public void givenRealTask_whenUpdatedTask_thenSucceed() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setId(1);
		taskDTO.setTitle(TASK_TITLE_TEST + " modified");
		TaskDTO updated;
		try {
			updated = taskManagerService.updateTask(taskDTO);
			assertThat(updated.getId()).isEqualTo(1);
			verify(taskEntityRepository).save(mockedTask);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void givenFakeTask_whenUpdatedTask_thenFail() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setId(10);
		taskDTO.setTitle(TASK_TITLE_TEST + " modified");
		try {
			taskManagerService.updateTask(taskDTO);
			fail();
		} catch (Exception e) {
			assertThat(e).isInstanceOf(TaskNotFoundException.class);
		}
	}

	@Test
	public void givenRealTask_whenDeleteTask_thenSucceed() {
		try {
			boolean result = taskManagerService.deleteTask(1);
			assertThat(result).isTrue();
			verify(taskEntityRepository).existsById(1);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void givenFakeTask_whenDeleteTask_thenFail() {
		try {
			taskManagerService.deleteTask(2);
			fail();
		} catch (Exception e) {
			assertThat(e).isInstanceOf(TaskNotFoundException.class);
		}
	}

	@Test
	public void givenRealTask_whenFinishTask_thenFail() {
		try {
			boolean result = taskManagerService.finishTask(1);
			assertThat(result).isTrue();
			verify(taskEntityRepository).countTaskEntityByIdAndFinished(1, true);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void givenFinishedTask_whenFinishTask_thenFail() {
		try {
			taskManagerService.finishTask(2);
			fail();
		} catch (Exception e) {
			assertThat(e).isInstanceOf(BusinessException.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenConditions_whenPageZero_thenReturnData() {
		Date now = new Date();
		Pageable pageZero = PageRequest.of(0, 10);
		List<TaskDTO> results = taskManagerService.searchTasks(TASK_TITLE_TEST, true, now, now, pageZero);
		assertThat(results).isNotEmpty();
		verify(taskEntityRepository).findAll(any(Specification.class), eq(pageZero));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenConditions_whenPageOne_thenReturnNothing() {
		Date now = new Date();
		Pageable pageOne = PageRequest.of(1, 10);
		List<TaskDTO> results = taskManagerService.searchTasks(TASK_TITLE_TEST, true, now, now, pageOne);
		assertThat(results).isEmpty();
		verify(taskEntityRepository).findAll(any(Specification.class), eq(pageOne));
	}
}
