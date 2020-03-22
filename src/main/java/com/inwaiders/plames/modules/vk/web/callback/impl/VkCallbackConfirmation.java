package com.inwaiders.plames.modules.vk.web.callback.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;
import com.inwaiders.plames.modules.vk.web.callback.VkCallback;
import com.inwaiders.plames.modules.vk.web.group.ajax.VkGroupWebAjaxLongPoll;

public class VkCallbackConfirmation extends VkCallback{

	public String run(VkGroup group, JsonNode json) {
		
		VkGroupWebAjaxLongPoll.onGroupVerificated(group);
		
		return group.getTestKey();
	}
	
	public String getType() {
		
		return "confirmation";
	}
}
