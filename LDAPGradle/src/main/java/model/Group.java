package model;

import java.util.ArrayList;
import java.util.List;

public class Group {

	private String groupName;
	private List<User> users = new ArrayList<User>();
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
}
