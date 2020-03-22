package com.inwaiders.plames.modules.vk.dao.profile;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.vk.domain.profile.VkProfile;

@Service
public class VkProfileRepositoryInjector {

	@Autowired
	private VkProfileRepository repository;
	
	@PostConstruct
	private void inject() {
		
		VkProfile.setRepository(repository);
	}
}
