package com.inwaiders.plames.modules.vk.web.group.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@RestController
@RequestMapping("api/vk/rest")
public class VkGroupRestController {

//	@Autowired
//	private VkGroupRepository vkGroupRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/groups/{id}", produces = "application/json; charset=UTF-8")
	public ObjectNode get(@PathVariable long id) {
		
		VkGroup group = VkGroup.getById(id);
	
		ObjectNode node = mapper.createObjectNode();
		
			node.put("name", group.getName());
			node.put("testKey", group.getTestKey());
			node.put("token", group.getToken());
			node.put("id", group.getId());
			node.put("groupId", group.getGroupId());
			node.put("active", group.isActive());
			node.put("secret", group.getSecret());
			
		return node;
	}
	
	@PostMapping(value = "/groups")
	public ObjectNode create(@RequestBody VkGroup group) {

		group.save();
		
		group = VkGroup.getByVkId(group.getGroupId());
		
		return get(group.getId());
	}
	
	@PutMapping(value = "/groups/{id}") 
	public ResponseEntity save(@PathVariable long id, @RequestBody JsonNode node) {
		
		VkGroup group = VkGroup.getById(id);
	
		if(group == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		if(node.has("name") && node.get("name").isTextual()) {
		
			group.setName(node.get("name").asText());
		}
		
		if(node.has("token") && node.get("token").isTextual()) {
			
			group.setToken(node.get("token").asText());
		}
		
		if(node.has("testKey") && node.get("testKey").isTextual()) {
			
			group.setTestKey(node.get("testKey").asText());
		}
		
		if(node.has("secret") && node.get("secret").isTextual()) {
			
			group.setSecret(node.get("secret").asText());
		}
		
		group.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/groups/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		VkGroup group = VkGroup.getById(id);
		
		if(group != null) {
			
			group.delete();
		
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
