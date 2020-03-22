package com.inwaiders.plames.modules.vk.dao.group;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@Repository
public interface VkGroupRepository extends JpaRepository<VkGroup, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT g FROM VkGroup g WHERE g.groupId = :groupId AND g.deleted != true")
	public VkGroup getByVkId(@Param(value = "groupId") long groupId);

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT g FROM VkGroup g WHERE g.id = :id AND g.deleted != true")
	public VkGroup getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT g FROM VkGroup g WHERE g.deleted != true")
	public List<VkGroup> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM VkGroup g WHERE g.deleted != true")
	public long count();
}