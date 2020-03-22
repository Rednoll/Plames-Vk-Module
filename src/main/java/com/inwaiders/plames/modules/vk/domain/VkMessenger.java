package com.inwaiders.plames.modules.vk.domain;

import javax.persistence.Entity;

import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;
import com.inwaiders.plames.modules.vk.domain.profile.VkProfile;

@Entity
public class VkMessenger extends MessengerImpl<VkProfile> {

	@Override
	public String getWebDescription() {
		
		return "- "+PlamesLocale.getSystemMessage("messenger.vk.description.profiles", VkProfile.getCount())+"<br/>- "+PlamesLocale.getSystemMessage("messenger.vk.description.groups", VkGroup.getCount());
	}
	
	@Override
	public String getName() {
		
		return "vkontakte";
	}
	
	@Override
	public String getType() {
		
		return "vk";
	}
}
