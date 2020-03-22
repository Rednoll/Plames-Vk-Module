package com.inwaiders.plames.modules.vk.domain.group;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.api.messenger.MessengerException;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardButton;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardButton.Priority;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardCommandButton;
import com.inwaiders.plames.api.messenger.keyboard.MessengerKeyboard;
import com.inwaiders.plames.api.messenger.keyboard.KeyboardLinkButton;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.modules.vk.dao.group.VkGroupRepository;
import com.inwaiders.plames.modules.vk.domain.profile.VkProfile;

@Cache(region = "messengers-additionals-cache-region", usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "vk_groups")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VkGroup {

	private static transient ObjectMapper mapper = new ObjectMapper();
	
	private static transient VkGroupRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "group_id")
	@JsonAlias("group_id")
	private long groupId = -1;

	@Column(name = "name")
	@JsonAlias("name")
	private String name = null;
	
	@Column(name = "secret")
	private String secret = null;
	
	@Column(name = "test_key")
	@JsonAlias("test_key")
	private String testKey = null;
	
	@Column(name = "token")
	private String token = null;
	
	@Column(name = "active")
	private boolean active = false;
	
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinTable(name = "vk_profiles_groups_mtm", joinColumns = @JoinColumn(name = "vk_group_id"), inverseJoinColumns = @JoinColumn(name = "vk_profile_id"))
	@JsonIgnore
	private Set<VkProfile> members = new HashSet<>();
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VkGroup other = (VkGroup) obj;
		return active == other.active && deleted == other.deleted && groupId == other.groupId
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(secret, other.secret) && Objects.equals(testKey, other.testKey)
				&& Objects.equals(token, other.token);
	}

	public boolean fromUser(VkProfile profile, String text) {
		
		if(!isActive()) return false;
	
		profile.fromUser(text);
		
		return true;
	}
	
	private String parseKeyboard(MessengerKeyboard keyboard) {
		
		if(keyboard == null) return null;
		
		List<List<KeyboardButton>> matrix = keyboard.getButtonsMatrix();
	
		ObjectNode node = mapper.createObjectNode();
			node.put("one_time", keyboard.isOnetime());
			node.put("inline", false);
	
			ArrayNode jsonButtonsRows = mapper.createArrayNode();
	
				for(List<KeyboardButton> columns : matrix) {
					
					ArrayNode jsonButtonsColumns = mapper.createArrayNode();
					
					for(KeyboardButton button : columns) {
						
						ObjectNode jsonButton = mapper.createObjectNode();
							
							if(button.getPriority() == Priority.PRIMARY) {
							
								jsonButton.put("color", "primary");
							}
							else if(button.getPriority() == Priority.SECONDARY) {
								
								jsonButton.put("color", "secondary");
							}
						
							ObjectNode action = mapper.createObjectNode();
								action.put("label", button.getLabel());
								action.put("payload", "\""+button.getMark()+"\"");
							
								if(button instanceof KeyboardCommandButton) {
									
									action.put("type", "text");
								}
								else if(button instanceof KeyboardLinkButton) {
									
									action.put("type", "open_link");
									action.put("link", ((KeyboardLinkButton) button).getUrl());
								}
								
							jsonButton.put("action", action);
					
						jsonButtonsColumns.add(jsonButton);
					}
					
					jsonButtonsRows.add(jsonButtonsColumns);
				}
				
			node.put("buttons", jsonButtonsRows);
			
		return node.toString();
	}
	
	public boolean sendMessage(Message message) throws MessengerException{
		
		if(!isActive()) return false;
		
		VkProfile destination = (VkProfile) message.getReceiver();
		
		try {
			
			String keyboardData = parseKeyboard(message.getKeyboard());
			
			//TODO: RANDOM STUB!
			sendMessage(destination.getVkId(), message.getDisplayText(), keyboardData, new Random().nextLong());
			
			message.setDeliveryDate(System.currentTimeMillis());
			message.setDelivered(true);
			
			return true;
		}
		catch(MessengerException e) {
			
			throw new MessengerException("Vk api error");
		}
	}
	
	private boolean sendMessage(long peerId, String text, String keyboard, long random_id) throws MessengerException{
		
		if(!isActive()) return false;
		
		StringBuilder builder = new StringBuilder();
			builder.append("https://api.vk.com/method/messages.send?");
			builder.append("access_token="+getToken());
			builder.append("&peer_id="+peerId);
			builder.append("&v=5.101");
			builder.append("&random_id="+random_id);
			
			try {
				
				if(keyboard != null && !keyboard.isEmpty()) {
					
					builder.append("&keyboard="+URLEncoder.encode(keyboard, "UTF-8"));
				}
				
				builder.append("&message="+URLEncoder.encode(text, "UTF-8"));
			}
			catch(UnsupportedEncodingException e1) {
				
				e1.printStackTrace();
			}
	
		try {

			HttpURLConnection con = (HttpURLConnection) new URL(builder.toString()).openConnection();
				con.setRequestMethod("GET");
				
				//Not work without it....
				InputStream in = con.getInputStream();
				
				in.close();
				//
				
			return true;
		}
		catch(IOException e) {
			
			throw new MessengerException("Vk api error");
		}
	}
	
	public long getVkMembersCount() {

		StringBuilder builder = new StringBuilder();
			builder.append("https://api.vk.com/method/groups.getMembers?");
			builder.append("access_token="+getToken());
			builder.append("&v=5.101");
			builder.append("&group_id="+getGroupId());
			builder.append("&count=0");
	
		RestTemplate template = new RestTemplate();
		
		ResponseEntity<JsonNode> response = template.getForEntity(builder.toString(), JsonNode.class);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			
			return response.getBody().get("response").get("count").asLong();
		}
		
		return -1;
	}
	
	public String getDescription(PlamesLocale locale) {
		
		String result = "";
		
		long vkMembersCount = getVkMembersCount();
		
		double membersPerSubsRaw = (double) members.size()/(double) vkMembersCount;
		
		String membersPerSubs = String.valueOf(membersPerSubsRaw);
		
		if(membersPerSubs.length() > 4) {
			
			membersPerSubs = membersPerSubs.substring(0, 4);
		}
		
		result += "- "+locale.getMessage("vk_group.description.profiles", vkMembersCount, members.size());
		result += "<br/>";
		result += "- "+locale.getMessage("vk_group.description.mps", membersPerSubs);
		result += "<br/>";
		result += "- "+locale.getMessage("vk_group.description.test", getTestKey());
		result += "<br/>";
		result += "- "+locale.getMessage("vk_group.description.secret", getSecret());
		result += "<br/>";
		result += "- "+locale.getMessage("vk_group.description.token", (token.substring(0, 5)+"..."+token.substring(token.length()-5, token.length())));
		
		return result;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public void setMembers(Set<VkProfile> members) {
		
		this.members = members;
	}
	
	public Set<VkProfile> getMembers() {
	
		return this.members;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}
	
	public boolean isActive() {
		
		return this.active;
	}
	
	public void setToken(String token) {
		
		this.token = token;
	}
	
	public String getToken() {
		
		return this.token;
	}
	
	public void setTestKey(String testKey) {
		
		this.testKey = testKey;
	}
	
	public String getTestKey() {
		
		return this.testKey;
	}
	
	public void setSecret(String secret) {
		
		this.secret = secret;
	}
	
	public String getSecret() {
		
		return this.secret;
	}

	public void setGroupId(long id) {
		
		this.groupId = id;
	}
	
	public long getGroupId() {
		
		return this.groupId;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		if(!deleted) {
		
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
		repository.save(this);
	}
	
	public static VkGroup create() {
		
		VkGroup profile = new VkGroup();
		
		profile = repository.saveAndFlush(profile);
		
		return profile;
	}
	
	public static VkGroup getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static VkGroup getByVkId(long id) {
		
		return repository.getByVkId(id);
	}
	
	public static List<VkGroup> getAll() {
		
		return repository.findAll();
	}
	
	public static long getCount() {
		
		return repository.count();
	}
	
	public static void setRepository(VkGroupRepository rep) {
		
		repository = rep;
	}
}
