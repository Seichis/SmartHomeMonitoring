package dto;

import java.io.Serializable;

public class UserDTO implements Serializable {

	private String name;
	private String password;
	private String role;
	private boolean validated;
	
	private static final long serialVersionUID = 1L;

	public UserDTO() {
	}	
	public UserDTO(String n, String p, String r) {
		this.name = n;
		this.password = p;
		this.role = r;
	}
   

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean getValidated(){
		return validated;
	}
	public void setValidated(boolean val){
		this.validated = val;
	}
}
