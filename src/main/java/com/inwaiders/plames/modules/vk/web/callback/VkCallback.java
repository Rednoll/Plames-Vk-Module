package com.inwaiders.plames.modules.vk.web.callback;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.vk.domain.group.VkGroup;

public abstract class VkCallback {

	public abstract String run(VkGroup group, JsonNode json);

	public abstract String getType();
}
