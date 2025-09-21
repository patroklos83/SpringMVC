package com.patroclos.dto;

import java.time.Instant;
import java.util.List;
import com.patroclos.utils.DateUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDTO {
	
	private String username;
	private Instant created;
	private String relativeTime;
	private String event;
	private String message;
	private List<GroupDTO> groups;
	
}
