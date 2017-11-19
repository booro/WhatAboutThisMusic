package message;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = -1219754689171046111L;
	
	String name;
	
	public User(){
		
	}
	
	public User(String name){
		this.name = name;
	}
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
