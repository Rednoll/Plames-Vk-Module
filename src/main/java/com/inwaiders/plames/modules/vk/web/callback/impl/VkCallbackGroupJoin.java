package com.inwaiders.plames.modules.vk.web.callback.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import com.inwaiders.plames.domain.messenger.profile.procedures.ProfileBindProcedure;
import com.inwaiders.plames.modules.vk.domain.VkMessenger;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;
import com.inwaiders.plames.modules.vk.domain.profile.VkProfile;
import com.inwaiders.plames.modules.vk.web.callback.VkCallback;
import com.inwaiders.plames.system.utils.MessageUtils;

public class VkCallbackGroupJoin extends VkCallback{
	
	@Override
	public String run(VkGroup group, JsonNode json) {
		
		long userId = json.get("user_id").asLong();
		String type = json.get("join_type").asText();
		
		if(type.equals("join") || type.equals("approved") || type.equals("accepted")) {
		
			VkMessenger messenger = (VkMessenger) MessengerImpl.getByType("vk");
			
			VkProfile profile = VkProfile.getByVkId(userId);
			
			if(profile != null) {
			
				User user = profile.getUser();
				
				profile.joinGroup(group);
				
				if(user != null) {
				
					if(profile.getGroups().size() == 1) {
						
//						MessageUtils.send(profile, "Добро пожаловать назад, "+user.getNickname()+"!");
					}
				}
				else {
					
					ProfileBindProcedure bindingProcedure = ProfileBindProcedure.create(profile);
						bindingProcedure.begin();
			
					profile.setCurrentProcedure(bindingProcedure);
				}
			}
			else { 
				
				profile = VkProfile.create(userId);
					profile.joinGroup(group);
				
				ProfileBindProcedure bindingProcedure = ProfileBindProcedure.create(profile);
					bindingProcedure.begin();
			
				profile.setCurrentProcedure(bindingProcedure);
			}
			
			profile.save();
		}
		
		return null;
	}

	@Override
	public String getType() {
		
		return "group_join";
	}

}
