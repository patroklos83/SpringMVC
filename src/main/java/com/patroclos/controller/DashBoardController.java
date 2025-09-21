package com.patroclos.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.patroclos.controller.core.BaseController;
import com.patroclos.dto.UserDTO;
import com.patroclos.dto.ai.ChatRequestDTO;
import com.patroclos.dto.ai.ChatRequestDTO.Message;
import com.patroclos.utils.AIUtil;

@Controller
public class DashBoardController extends BaseController {
	
	@GetMapping("/dashboard")
	public String pageLoadGet(ModelMap model) throws Exception {
		setWelcomeMessage(model);
		return "main/dashboard";
	}
	
	@PostMapping("/dashboard")
	public String pageLoadPost(ModelMap model) throws Exception {
		setWelcomeMessage(model);
		return "main/dashboard";
	}
	
	private void setWelcomeMessage(ModelMap model) throws Exception {
		UserDTO user = AuthenticationFacade.getLoggedUser();
		model.addAttribute("WelcomeMessage", String.format("Welcome %s!", user.getUsername()));
		
		
		var activities = SystemFacade.loadLatestActivitiesFromDatabase();
		if (activities == null || activities.size() == 0 ) {
			return;
		}
		
		var activitySummaries = activities.stream()
				.filter(a -> a.getSummary() != null)
				.map(a -> a.getSummary()).toList();
		
		if (activitySummaries == null || activitySummaries.size() == 0) {
			return;
		}
		
		
		AIUtil aiUtil = new AIUtil();
		var request = ChatRequestDTO.builder()
		.model("ai/gemma3")
		.messages(new ArrayList<>())
		.build();
		Message m = new Message();
		m.setRole("user");
		m.setContent("You are an AI assistant that helps users to understand the latest activities in their system. "
				+ "Based on the following activity summaries, provide a concise and friendly welcome message for the dashboard, "
				+ "highlighting key actions and encouraging further engagement. "
				+ "Make it engaging and relevant to the user's recent activities. "
				+ "Also suggest one or two actions the user might want to take next based on these activities. "
				+ "Kepp an eye on security-related activities and mention them if relevant. "
				+ "Here are the activity summaries: " + activitySummaries.toString());
		//+ notifications.stream().map(n -> n.getMessage()).toList().toString());
		request.getMessages().add(m);
		String response = aiUtil.callModelRunner(request);
		if (response != null && response.length() > 0) {
			model.addAttribute("message", 
					response.replace("\\", "\\\\")   // backslash
				    .replace("\"", "\\\"")   // double quote
				    .replace("\r", "\\r")    // carriage return
				    .replace("\n", "\\n"));
		}
	}

}
