package Model;

import java.io.Serializable;

public class User implements Serializable {
	private int userID;
	private String username;
	private String bio;
	private String password;
	private String profile_picture;
	private String created_at;
	private String email;
	private String name;

	public User(int id, String username, String pw, String bio, String email, String profile_pic, String created_at,
			String name) {
		this.userID = id;
		this.username = username;
		this.bio = bio;
		this.password = pw;
		this.created_at = created_at;
		this.email = email;
		this.profile_picture = profile_pic;
		this.name = name;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfile_picture() {
		return profile_picture;
	}

	public void setProfile_picture(String profile_picture) {
		this.profile_picture = profile_picture;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
