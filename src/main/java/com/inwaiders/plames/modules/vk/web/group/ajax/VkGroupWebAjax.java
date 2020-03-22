package com.inwaiders.plames.modules.vk.web.group.ajax;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@RestController
@RequestMapping("web/controller/ajax/vk/group")
public class VkGroupWebAjax {

	@PostMapping("/active")
	public ResponseEntity activeToggle(@RequestBody JsonNode json) {
		
		if(!json.has("group") || !json.get("group").isNumber()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
		if(!json.has("active") || !json.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
	
		long groupId = json.get("group").asLong();
		boolean active = json.get("active").asBoolean();
	
		VkGroup group = VkGroup.getById(groupId);
		
		if(group != null) {
			
			group.setActive(active);
			group.save();
		
			return new ResponseEntity(HttpStatus.OK);
		}
		
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/description", consumes = "application/json;charset=UTF-8", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> description(@RequestBody JsonNode json) {
	
		if(!json.has("group") || !json.get("group").isNumber()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
		
		long groupId = json.get("group").asLong();
		
		VkGroup group = VkGroup.getById(groupId);
		
		if(group != null) {
			
			try {
				
				return new ResponseEntity<String>(group.getDescription(PlamesLocale.getSystemLocale()), HttpStatus.OK);
			}
			catch(Exception e) {
				
				return new ResponseEntity<String>("Ошибка загрузки данных", HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
}
