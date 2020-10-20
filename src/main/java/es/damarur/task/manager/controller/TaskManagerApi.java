package es.damarur.task.manager.controller;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.damarur.task.manager.dto.TaskDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "task", description = "the task API")
public interface TaskManagerApi {

	@ApiOperation(value = "adds a task", nickname = "addTask", notes = "Adds a task to the system", response = TaskDTO.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "task created", response = TaskDTO.class),
			@ApiResponse(code = 400, message = "invalid input, object invalid"),
			@ApiResponse(code = 409, message = "an existing task already exists") })
	@PostMapping(value = "/task", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<TaskDTO> addTask(@ApiParam(value = "Task to add") @Valid @RequestBody TaskDTO task);

	@ApiOperation(value = "deletes a task", nickname = "deleteTask", notes = "Deletes a task from the system")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "task deleted"),
			@ApiResponse(code = 404, message = "task not found") })
	@DeleteMapping(value = "/task/{id}")
	ResponseEntity<Void> deleteTask(@ApiParam(value = "Task's id to delete", required = true) @PathVariable("id") Integer id) throws TaskNotFoundException;

	@ApiOperation(value = "Mark a task as finished", nickname = "finishTask", notes = "Mark a task as finished")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "task finished"),
			@ApiResponse(code = 404, message = "task not found"),
			@ApiResponse(code = 409, message = "task already finished") })
	@PutMapping(value = "/task/finish/{id}")
	ResponseEntity<Void> finishTask(@ApiParam(value = "Task's id to mark as finished", required = true) @PathVariable("id") Integer id) throws BusinessException;

	@ApiOperation(value = "searches tasks", nickname = "searchTask", notes = "By passing in the appropriate options, you can find tasks ", response = TaskDTO.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "search results matching criteria", response = TaskDTO.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "bad input parameter") })
	@GetMapping(value = "/task", produces = { "application/json" })
	ResponseEntity<List<TaskDTO>> searchTask(
			@ApiParam(value = "pass an optional search string for looking up tasks") @Valid @RequestParam(value = "query", required = false) String query,
			@ApiParam(value = "search finished/unfinished tasks") @Valid @RequestParam(value = "finished", required = false) Boolean finished,
			@ApiParam(value = "to filter from date") @Valid @RequestParam(value = "fromDate", required = false) String fromDate,
			@ApiParam(value = "to filter to date") @Valid @RequestParam(value = "toDate", required = false) String toDate,
			@Min(1) @ApiParam(value = "number of records to skip for pagination") @Valid @RequestParam(value = "size", required = true) Integer size,
			@Min(1) @ApiParam(value = "page for pagination") @Valid @RequestParam(value = "page", required = true) Integer page) throws ParseException;

	@ApiOperation(value = "updates a task", nickname = "updateTask", notes = "Update a task from the system", response = TaskDTO.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "task updated", response = TaskDTO.class),
			@ApiResponse(code = 400, message = "invalid input, object invalid"),
			@ApiResponse(code = 404, message = "task not found") })
	@PutMapping(value = "/task", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<TaskDTO> updateTask(@ApiParam(value = "Task to update") @Valid @RequestBody TaskDTO task) throws TaskNotFoundException;

}
