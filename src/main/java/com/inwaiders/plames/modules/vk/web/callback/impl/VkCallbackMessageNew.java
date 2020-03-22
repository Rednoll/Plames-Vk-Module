package com.inwaiders.plames.modules.vk.web.callback.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardButton;
import com.inwaiders.plames.api.messenger.keyboard.MessengerKeyboard;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;
import com.inwaiders.plames.modules.vk.domain.profile.VkProfile;
import com.inwaiders.plames.modules.vk.web.callback.VkCallback;

public class VkCallbackMessageNew extends VkCallback{

	@Override
	public String run(VkGroup group, JsonNode json) {
		
		long senderId = json.get("peer_id").asLong();
		String text = json.get("text").asText();
		
		VkProfile profile = (VkProfile) VkProfile.getByVkId(senderId);
		
		if(profile == null) {
		
			profile = VkProfile.create(senderId);
				profile.joinGroup(group);
		}
		
		if(json.has("payload")) {
			
			MessengerKeyboard keyboard = MessengerKeyboard.load(profile.getId());
		
			if(keyboard != null) {
				
				String payload = json.get("payload").asText();
				
				payload = payload.substring(1, payload.length()-1);
				
				KeyboardButton button = keyboard.getButtonByMark(payload);
			
				if(button != null) {
					
					button.action(profile);
				}
			}
		}
		else {
			
			group.fromUser(profile, text);
		}
		
		return null;
	}

	@Override
	public String getType() {
		
		return "message_new";
	}
}
