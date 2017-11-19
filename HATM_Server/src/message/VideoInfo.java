package message;

import java.io.Serializable;

public class VideoInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3513977025432680670L;
	private String vidId;
	private User user;
	private int vidLength;
	private int currentTime;
	private boolean isVidLengthSet;
	
	public VideoInfo(){
		isVidLengthSet=false;
	}

	public VideoInfo(String vidId){
		this.vidId = vidId;
		isVidLengthSet=false;
	}
	
	public VideoInfo(String vidId, User user){
		this.vidId = vidId;
		this.user = user;
		isVidLengthSet=false;
	}
	
	public boolean isVidLengthSet() {
		return isVidLengthSet;
	}

	public void setVidLengthSet(boolean isVidLengthSet) {
		this.isVidLengthSet = isVidLengthSet;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
	public String getVidId() {
		return vidId;
	}
	public void setVidId(String vidId) {
		this.vidId = vidId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getVidLength() {
		return vidLength;
	}
	public void setVidLength(int vidLength) {
		this.vidLength = vidLength;
		isVidLengthSet = true;
	}
	
	public void addSecToCurrentTime(){
		currentTime++;
	}
	
	public boolean isCurrentVidFinnished(){
		if(vidLength > 0 && (currentTime/(vidLength+5)) >= 1){
			return true;
		}
		return false;
	}
}
