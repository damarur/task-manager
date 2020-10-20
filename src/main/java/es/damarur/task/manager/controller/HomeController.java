package es.damarur.task.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping(value = "/")
	public String index() {
		return "redirect:swagger-ui/index.html";
	}
}
