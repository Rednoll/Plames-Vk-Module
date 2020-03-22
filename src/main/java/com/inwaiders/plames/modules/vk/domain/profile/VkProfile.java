package com.inwaiders.plames.modules.vk.domain.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.api.messenger.MessengerException;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.domain.messenger.profile.impl.UserProfileBase;
import com.inwaiders.plames.modules.vk.dao.profile.VkProfileRepository;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

@Component
@Entity
@Table(name = "vk_profiles")
public class VkProfile extends UserProfileBase {

	@Autowired
	private static transient VkProfileRepository repository;
	
	@Column(name = "vk_id")
	private long vkId = -1;
	
	@ManyToMany(cascade = CascadeType.MERGE, mappedBy = "members")
	private List<VkGroup> groups = new ArrayList<>();
	
	@JoinColumn(name = "priority_group_id")
	@ManyToOne(cascade = CascadeType.ALL)
	private VkGroup priorityGroup = null;
	
	@Override
	public int hashCode() {
		return Objects.hash(getId(), deleted);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VkProfile other = (VkProfile) obj;
		return isAccessible() == other.isAccessible() && Objects.equals(this.getId(), other.getId()) && vkId == other.vkId && deleted == other.deleted;
	}
	
	public void joinGroup(VkGroup group) {
		
		this.groups.add(group);
		group.getMembers().add(this);
		
		if(this.groups.size() == 1) {
			
			setPriorityGroup(group);
		}
		
		this.save();
		group.save();	
	}
	
	public boolean receiveMessage(Message message) throws MessengerException {
		
		return receiveByGroup(message);
	}
	
	private boolean receiveByGroup(Message message) throws MessengerException{

		VkGroup group = this.pickGroup();
		
		if(group == null) {
			
			throw new MessengerException(PlamesLocale.getSystemMessage("$profile.vk.group_nf"));
		}
		
		return group.sendMessage(message);
	}
	
	public VkGroup pickGroup() {
		
		VkGroup group = getPriorityGroup();
		
		if(group != null && group.isActive()) return group;
		
		for(VkGroup suspect : getGroups()) {
			
			if(suspect.isActive()) {
			
				return suspect;
			}
		}
		
		return null;
	}
	
	public void setPriorityGroup(VkGroup group) {
		
		this.priorityGroup = group;
	}
	
	public VkGroup getPriorityGroup() {
		
		return this.priorityGroup;
	}
	
	public void setGroups(List<VkGroup> groups) {
		
		this.groups = groups;
	}
	
	public List<VkGroup> getGroups() {
	
		return this.groups;
	}
	
	public void setVkId(long vkId) {
		
		this.vkId = vkId;
	}
	
	public long getVkId() {
		
		return this.vkId;
	}
	
	public String getHumanSign() {
		
		return "[vk] VkId: "+getVkId();
	}
	
	public String getMessengerType() {
		
		return "vk";
	}
	
	public static VkProfile create(long senderId) {
		
		VkProfile profile = new VkProfile();
			profile.setVkId(senderId);
		
		profile = repository.saveAndFlush(profile);
		
		return profile;
	}
	
	public static VkProfile getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static VkProfile getByVkId(long id) {
		
		return repository.getByVkId(id);
	}
	
	public static List<VkProfile> getAll() {
		
		return repository.findAll();
	}
	
	public static long getCount() {
		
		return repository.count();
	}
	
	public static void setRepository(VkProfileRepository rep) {
		
		repository = rep;
	}
}
