package Model;

public class DetailMessage {
	private int userLogin, sender, receiver, messageID;
	private String message_text;
	private String send_time;
	private String messageType;

	public DetailMessage(int messageID, int userLogin, int sender, int receiver, String text, String sent_time,
			String messageType) {
		this.message_text = text;
		this.receiver = receiver;
		this.send_time = sent_time;
		this.userLogin = userLogin;
		this.sender = sender;
		this.messageID = messageID;
		this.messageType = messageType;
	}

	public int getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(int userLogin) {
		this.userLogin = userLogin;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public String getMessage_text() {
		return message_text;
	}

	public void setMessage_text(String message_text) {
		this.message_text = message_text;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
