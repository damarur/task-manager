package es.damarur.task.manager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(HomeController.class)
@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
public class HomeControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void shouldReturnDefaultMessage() throws Exception {
		mvc.perform(get("/")).andExpect(status().is3xxRedirection());
	}

}
