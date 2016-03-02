package org.test.agf.datasource.objects;

public class User {
	
	private String useName;
	private String email;
	
	public User(String useName, String email) {
		super();
		this.useName = useName;
		this.email = email;
	}
	
	public String getUseName() {
		return useName;
	}
	
	public void setUseName(String useName) {
		this.useName = useName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

}
