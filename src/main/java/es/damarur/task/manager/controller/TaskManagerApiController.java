package es.damarur.task.manager.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.damarur.task.manager.dto.TaskDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;
import es.damarur.task.manager.service.TaskManagerService;
import es.damarur.task.manager.util.TMConstant;

@Controller
@Validated
public class TaskManagerApiController implements TaskManagerApi {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	@Qualifier("taskManagerService")
	private TaskManagerService service;

	@Override
	public ResponseEntity<TaskDTO> addTask(@Valid @RequestBody TaskDTO task) {
		if (task.getId() == null) {
			TaskDTO createdTask = service.createTask(task);
			return new ResponseEntity<>(createdTask, HttpStatus.OK);
		} else {
			throw new IllegalStateException(TMConstant.TASK_CREATE_NOT_ID);
		}
	}

	@Override
	public ResponseEntity<Void> deleteTask(@PathVariable("id") Integer id) throws TaskNotFoundException {
		service.deleteTask(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> finishTask(@PathVariable("id") Integer id) throws BusinessException {
		service.finishTask(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<TaskDTO>> searchTask(
			@Valid @RequestParam(value = "query", required = false) String query,
			@Valid @RequestParam(value = "finished", required = false) Boolean finished,
			@Valid @RequestParam(value = "fromDate", required = false) String fromDate,
			@Valid @RequestParam(value = "toDate", required = false) String toDate,
			@Min(1) @Valid @RequestParam(value = "size", required = true) Integer size,
			@Min(1) @Valid @RequestParam(value = "page", required = true) Integer page) throws ParseException {
		Pageable pageRequest = PageRequest.of(page - 1, size);
		Date from = null;
		if (StringUtils.isNotBlank(fromDate)) {
			from = SDF.parse(fromDate);
		}
		Date to = null;
		if (StringUtils.isNotBlank(toDate)) {
			to = SDF.parse(toDate);
		}
		List<TaskDTO> results = service.searchTasks(query, finished, from, to, pageRequest);
		return new ResponseEntity<>(results, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TaskDTO> updateTask(@Valid @RequestBody TaskDTO task) throws TaskNotFoundException {
		TaskDTO updatedTask = service.updateTask(task);
		return new ResponseEntity<>(updatedTask, HttpStatus.OK);
	}

}
