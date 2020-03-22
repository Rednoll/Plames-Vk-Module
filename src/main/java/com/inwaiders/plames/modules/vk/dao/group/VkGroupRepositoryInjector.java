package com.inwaiders.plames.modules.vk.dao.group;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@Service
public class VkGroupRepositoryInjector {

	@Autowired
	private VkGroupRepository repository;
	
	@PostConstruct
	private void inject() {
		
		VkGroup.setRepository(repository);
	}
}
