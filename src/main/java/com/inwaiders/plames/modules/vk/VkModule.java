package com.inwaiders.plames.modules.vk;

import com.inwaiders.plames.api.application.ApplicationAgent;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import com.inwaiders.plames.domain.module.impl.ModuleBase;
import com.inwaiders.plames.modules.vk.domain.VkMessenger;

public class VkModule extends ModuleBase implements ApplicationAgent {

	private static final VkModule instance = new VkModule();
	
	@Override
	public void init() {
		
		MessengerImpl mess = MessengerImpl.getByType("vk");
	
		if(mess == null) {
			
			VkMessenger vkMessenger = new VkMessenger();
			
			vkMessenger.save();
		}
	}
	
	public String getDescription() {
		
		return PlamesLocale.getSystemMessage("module.vk.description");
	}
	
	@Override
	public String getName() {
		
		return "Vk Integration";
	}

	@Override
	public String getLicenseKey() {
		
		return null;
	}

	@Override
	public long getId() {
	
		return 4521;
	}

	@Override
	public String getType() {
	
		return "integration";
	}

	@Override
	public String getVersion() {
		
		return "1V";
	}

	@Override
	public long getSystemVersion() {
		
		return 0;
	}
	
	public static VkModule getInstance() {
		
		return instance;
	}

	@Override
	public String getDisplayName() {
		
		return "Vkontakte";
	}

	@Override
	public String getTag() {
		
		return "vk";
	}
}
