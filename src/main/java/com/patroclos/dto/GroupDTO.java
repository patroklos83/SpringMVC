package com.patroclos.dto;

import java.util.List;

import com.patroclos.model.User;

public class GroupDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

    private String name;
	private List<UserDTO> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public List<UserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UserDTO> users) {
		this.users = users;
	} 
}