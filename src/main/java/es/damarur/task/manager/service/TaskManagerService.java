package es.damarur.task.manager.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import es.damarur.task.manager.dto.TaskDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;

public interface TaskManagerService {

	public TaskDTO createTask(TaskDTO taskDTO);

	public TaskDTO updateTask(TaskDTO taskDTO) throws TaskNotFoundException;

	public boolean deleteTask(Integer id) throws TaskNotFoundException;

	public boolean finishTask(Integer id) throws BusinessException;

	public List<TaskDTO> searchTasks(String query, Boolean finished, Date from, Date to, Pageable pageRequest);
}
