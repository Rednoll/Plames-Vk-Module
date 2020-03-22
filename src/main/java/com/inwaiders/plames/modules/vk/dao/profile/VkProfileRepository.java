package com.inwaiders.plames.modules.vk.dao.profile;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.vk.domain.profile.VkProfile;

@Repository
public interface VkProfileRepository extends JpaRepository<VkProfile, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT p FROM VkProfile p WHERE p.vkId = :vkId AND p.deleted != true")
	public VkProfile getByVkId(@Param(value = "vkId") long vkId);

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT p FROM VkProfile p WHERE p.id = :id AND p.deleted != true")
	public VkProfile getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT p FROM VkProfile p WHERE p.deleted != true")
	public List<VkProfile> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM VkProfile p WHERE p.deleted != true")
	public long count();
}
