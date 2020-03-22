package com.inwaiders.plames.modules.vk.web.group.ajax;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@RestController
@RequestMapping("web/controller/ajax/long_poll/vk")
public class VkGroupWebAjaxLongPoll {
	
	private static volatile Map<Long, DeferredResult<ResponseEntity>> verificationRequestsPool = new HashMap<>();
	
	@PostMapping("/group/verification")
	public DeferredResult<ResponseEntity> verification(@RequestBody JsonNode json) {
		
		DeferredResult<ResponseEntity> output = new DeferredResult<ResponseEntity>();
		
		if(!json.has("groupId") || !json.get("groupId").isNumber()) {
		
			output.setResult(new ResponseEntity(HttpStatus.BAD_REQUEST));
			return output;
		}
		
		long groupId = json.get("groupId").asLong();

		verificationRequestsPool.put(groupId, output);

		return output;
	}
	
	public static void onGroupVerificated(VkGroup group) {
		
		long groupId = group.getGroupId();
		
		DeferredResult<ResponseEntity> res = verificationRequestsPool.get(groupId);
	
		if(res != null) {
			
			res.setResult(new ResponseEntity<>(HttpStatus.ACCEPTED));
			verificationRequestsPool.remove(groupId);
		}
	}
}
