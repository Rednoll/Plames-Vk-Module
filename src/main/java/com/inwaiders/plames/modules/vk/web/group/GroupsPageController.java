package com.inwaiders.plames.modules.vk.web.group;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@Controller
public class GroupsPageController {

	@GetMapping("/vk/groups")
	public String mainPage(Model model) {
		
		List<VkGroup> groups = VkGroup.getAll();
		
		model.addAttribute("groups", groups);
		
		return "vk_groups";
	}
}
