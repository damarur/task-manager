package es.damarur.task.manager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import es.damarur.task.manager.dto.TaskDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;
import es.damarur.task.manager.model.TaskEntity;
import es.damarur.task.manager.repository.TaskEntityRepository;
import es.damarur.task.manager.repository.TaskEntitySpecifications;
import es.damarur.task.manager.util.TMConstant;

@Service
public class TaskManagerServiceImpl implements TaskManagerService {

	private TaskEntityRepository taskEntityRepository;

	public TaskManagerServiceImpl(TaskEntityRepository taskEntityRepository) {
		this.taskEntityRepository = taskEntityRepository;
	}

	@Override
	public TaskDTO createTask(TaskDTO taskDTO) {
		TaskEntity entity = new TaskEntity();
		return saveEntity(taskDTO, entity);
	}

	@Override
	public TaskDTO updateTask(TaskDTO taskDTO) throws TaskNotFoundException {
		Optional<TaskEntity> entity = taskEntityRepository.findById(taskDTO.getId());
		if (entity.isPresent()) {
			return saveEntity(taskDTO, entity.get());
		} else {
			throw new TaskNotFoundException(TMConstant.TASK_NOT_FOUND);
		}
	}

	/**
	 * Method to create and update tasks
	 * 
	 * @param taskDTO Object coming from frontend
	 * @param entity  Object to create or update
	 * @return Result object
	 */
	private TaskDTO saveEntity(TaskDTO taskDTO, TaskEntity entity) {
		BeanUtils.copyProperties(taskDTO, entity);
		TaskEntity saved = taskEntityRepository.save(entity);
		TaskDTO result = new TaskDTO();
		BeanUtils.copyProperties(saved, result);
		return result;
	}

	@Override
	public boolean deleteTask(Integer id) throws TaskNotFoundException {
		// Must check if entity exists
		if (taskEntityRepository.existsById(id)) {
			taskEntityRepository.deleteById(id);
			return true;
		} else {
			throw new TaskNotFoundException(TMConstant.TASK_NOT_FOUND);
		}
	}

	@Override
	public boolean finishTask(Integer id) throws BusinessException {
		int count = taskEntityRepository.countTaskEntityByIdAndFinished(id, true);
		if (count == 1) {
			throw new BusinessException("task.already.finished");
		}
		int result = taskEntityRepository.updateFinishedTask(true, new Date(), id);
		return result > 0;
	}

	@Override
	public List<TaskDTO> searchTasks(String title, Boolean finished, Date from, Date to, Pageable pageable) {
		Specification<TaskEntity> spec1 = TaskEntitySpecifications.finishDateGreaterThan(from);
		Specification<TaskEntity> spec2 = TaskEntitySpecifications.finishDateLessThan(to);
		Specification<TaskEntity> spec3 = TaskEntitySpecifications.startDateGreaterThan(from);
		Specification<TaskEntity> spec4 = TaskEntitySpecifications.startDateLessThan(to);
		Specification<TaskEntity> spec5 = TaskEntitySpecifications.targetDateGreaterThan(from);
		Specification<TaskEntity> spec6 = TaskEntitySpecifications.targetDateLessThan(to);
		Specification<TaskEntity> spec7 = TaskEntitySpecifications.finishedEquals(finished);
		Specification<TaskEntity> spec8 = TaskEntitySpecifications.titleLike(title);
		Specification<TaskEntity> spec = Specification.where(spec1).and(spec2).and(spec3).and(spec4).and(spec5)
				.and(spec6).and(spec7).and(spec8);
		Page<TaskEntity> page = taskEntityRepository.findAll(spec, pageable);
		if (page.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<TaskDTO> results = new ArrayList<>(page.getContent().size());
			for (TaskEntity entity : page.getContent()) {
				TaskDTO dto = new TaskDTO();
				BeanUtils.copyProperties(entity, dto);
				results.add(dto);
			}
			return results;
		}
	}

}
