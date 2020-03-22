package com.inwaiders.plames.modules.vk.web.callback;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;
import com.inwaiders.plames.modules.vk.web.callback.impl.VkCallbackConfirmation;
import com.inwaiders.plames.modules.vk.web.callback.impl.VkCallbackGroupJoin;
import com.inwaiders.plames.modules.vk.web.callback.impl.VkCallbackMessageNew;

@RestController
@RequestMapping("api/vk")
public class VkMessengerWebCallback {
	
	private Map<String, VkCallback> callbacks = new HashMap<>();
	
	public VkMessengerWebCallback() {
		
		registerCallback(new VkCallbackConfirmation());
		registerCallback(new VkCallbackMessageNew());
		registerCallback(new VkCallbackGroupJoin());
	}
	
	@PostMapping(value = "/callback")
	public ResponseEntity callback(@RequestBody JsonNode json) {
	
		if(!json.has("group_id") || !json.get("group_id").isNumber()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if(!json.has("secret") || !json.get("secret").isTextual()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if(!json.has("type") || !json.get("type").isTextual()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		if(!json.has("object")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		VkGroup group = VkGroup.getByVkId(json.get("group_id").asLong());
		
		if(group == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		if(!group.getSecret().equals(json.get("secret").asText())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		
		VkCallback callback = callbacks.get(json.get("type").asText());
		
		if(callback != null) {
			
			String result = callback.run(group, json.get("object"));
			
			if(result != null && !result.isEmpty()) {
			
				return new ResponseEntity<String>(result, HttpStatus.OK);
			}
			else {
				
				return new ResponseEntity<String>("OK", HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	public void registerCallback(VkCallback callback) {
		
		callbacks.put(callback.getType(), callback);
	}
}
