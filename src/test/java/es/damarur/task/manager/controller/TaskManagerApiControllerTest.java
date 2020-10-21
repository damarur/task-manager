package es.damarur.task.manager.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.damarur.task.manager.dto.TaskDTO;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskManagerApiController.class)
@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
public class TaskManagerApiControllerTest {

	private static final String TASK_TITLE_TEST = "Task title";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private TaskManagerApiController taskManagerApiController;

	@Test
	public void givenNewTask_whenTitleIsLong_thenSucceed() throws Exception {
		TaskDTO task = new TaskDTO();
		task.setTitle(TASK_TITLE_TEST);
		String body = new ObjectMapper().writeValueAsString(task);
		ResponseEntity<TaskDTO> response = new ResponseEntity<TaskDTO>(task, HttpStatus.OK);
		given(taskManagerApiController.addTask(task)).willReturn(response);
		mvc.perform(post("/task").content(body).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void givenNewTask_whenTitleIsShort_thenFail() throws Exception {
		TaskDTO task = new TaskDTO();
		task.setTitle(TASK_TITLE_TEST.substring(0, 4));
		String body = new ObjectMapper().writeValueAsString(task);
		ResponseEntity<TaskDTO> response = new ResponseEntity<TaskDTO>(task, HttpStatus.OK);
		given(taskManagerApiController.addTask(task)).willReturn(response);
		mvc.perform(post("/task").content(body).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void givenRealTask_whenDeleteTask_thenSucceed() throws Exception {
		given(taskManagerApiController.deleteTask(1)).willReturn(new ResponseEntity<Void>(HttpStatus.OK));
		mvc.perform(delete("/task/1")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void givenNoTask_whenDeleteTask_thenFail() throws Exception {
		given(taskManagerApiController.deleteTask(1)).willReturn(new ResponseEntity<Void>(HttpStatus.OK));
		mvc.perform(delete("/task/")).andExpect(status().is4xxClientError());
	}

	@Test
	public void givenRealTask_whenFinishTask_thenSucceed() throws Exception {
		given(taskManagerApiController.finishTask(1)).willReturn(new ResponseEntity<Void>(HttpStatus.OK));
		mvc.perform(put("/task/finish/1")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void givenNoTask_whenFinishTask_thenFail() throws Exception {
		given(taskManagerApiController.finishTask(1)).willReturn(new ResponseEntity<Void>(HttpStatus.OK));
		mvc.perform(put("/task/finish/")).andExpect(status().is4xxClientError());
	}

	@Test
	public void givenRealTask_whenUpdateTask_thenSucceed() throws Exception {
		TaskDTO task = new TaskDTO();
		task.setId(1);
		task.setTitle(TASK_TITLE_TEST);
		String body = new ObjectMapper().writeValueAsString(task);
		ResponseEntity<TaskDTO> response = new ResponseEntity<TaskDTO>(task, HttpStatus.OK);
		given(taskManagerApiController.updateTask(task)).willReturn(response);
		mvc.perform(put("/task").content(body).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void givenFakeTask_whenUpdateTask_thenFail() throws Exception {
		TaskDTO task = new TaskDTO();
		task.setId(2);
		task.setTitle(TASK_TITLE_TEST.substring(0, 4));
		String body = new ObjectMapper().writeValueAsString(task);
		ResponseEntity<TaskDTO> response = new ResponseEntity<TaskDTO>(task, HttpStatus.OK);
		given(taskManagerApiController.updateTask(task)).willReturn(response);
		mvc.perform(put("/task").content(body).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void givenSearchTask_whenAllIsValid_thenSucceed() throws Exception {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		TaskDTO task = new TaskDTO();
		task.setTitle(TASK_TITLE_TEST);
		List<TaskDTO> tasks = new ArrayList<TaskDTO>(1);
		tasks.add(task);
		ResponseEntity<List<TaskDTO>> response = new ResponseEntity<List<TaskDTO>>(tasks, HttpStatus.OK);
		given(taskManagerApiController.searchTask("", true, sdf.format(now), sdf.format(now), 10, 1))
				.willReturn(response);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("query", "");
		params.add("finished", "true");
		params.add("fromDate", sdf.format(now));
		params.add("toDate", sdf.format(now));
		params.add("size", "10");
		params.add("page", "1");
		mvc.perform(get("/task")).andExpect(status().is4xxClientError());
		mvc.perform(get("/task").params(params)).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$[0].title", is(task.getTitle())));
	}
}
