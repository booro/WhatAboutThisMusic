package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import message.Message;
import message.MessageType;
import message.User;
import message.VideoInfo;

public class MultiThreadedServer{

    protected int          serverPort   = 6767;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    private Vector<ObjectOutputStream> clients;
    private Vector<VideoInfo> vidQueue;
    private VideoInfo currentVid;

    public MultiThreadedServer(int port){
        this.serverPort = port;
        clients = new Vector<>();
        vidQueue = new Vector<>();
    }

    public void run()  throws SocketException{
    	
        openServerSocket();
        System.out.println("서버 가동중");
        vidScheduler();
        while(! isStopped()){
            Socket socket = null;
            try {         	
            	serverSocket.setReuseAddress(true);
            	socket = this.serverSocket.accept();
                System.out.println("클라이언트 접속! " + socket.toString());
            } catch (Exception e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    throw new SocketException();
                }
            }
            
            new Thread(
                new ServerReaderRunnable(socket)
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private boolean isStopped() {
        return this.isStopped;
    }

    public void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server");
            e.printStackTrace();
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            System.out.println("Cannot open port " + serverPort);
            e.printStackTrace();
        }
    }
    
    private synchronized void boradCast(Message msg) throws IOException {
        for (ObjectOutputStream coos : clients) {
            try {
            	System.out.println("브로드캐스팅");
            	coos.writeObject(msg);
            	coos.flush();
            } catch (Exception ex) {
            	System.out.println("브로드캐스팅중 에러 발생");
            }
        }
    }
    
    private synchronized boolean isVidLengthSet(){
    	return currentVid.isVidLengthSet();
    }
    
    private synchronized void setCurrentVidLength(int vidLength){
    	currentVid.setVidLength(vidLength);
    }
    
    private void vidScheduler(){
    	Timer vid_Timer = new Timer();
    	TimerTask vid_task = new TimerTask(){
    		int count = 0;
			@Override
			public void run() {
				//System.out.println("비디오 스케쥴러 현재 재생중 : " + currentVid.getVidId());
				
				if(currentVid==null || currentVid.isCurrentVidFinnished()){
					if(!vidQueue.isEmpty()){
						currentVid = vidQueue.remove(0);
						Message msg = new Message();
						msg.setType(MessageType.VIDID);
						msg.setVid(currentVid);
						msg.setUser(currentVid.getUser());
						try {
							boradCast(msg);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						count=0;
					}
					else{
						currentVid=null;
					}
				}
				else if(currentVid.isVidLengthSet()){
					count++;
					currentVid.setCurrentTime(count);
				}
				else if(currentVid!=null){
					count++;
				}
				
				if(currentVid!=null)
				System.out.println("현재 재생 시간 : " + currentVid.getCurrentTime());
			}
    	};
    	
    	vid_Timer.schedule(vid_task, 1000, 1000);
    	System.out.println("비디오 스케쥴러 가동중");
    }
    
    public class ServerReaderRunnable implements Runnable{

    	private Socket clientSocket = null;
        private ObjectInputStream input;
        private ObjectOutputStream output;

        public ServerReaderRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        
        public void sendMessage(Message msg) {
            try {
                output.writeObject(msg);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
        	try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                	
                	InputStream is = clientSocket.getInputStream();
                    input = new ObjectInputStream(is);
                	OutputStream os = clientSocket.getOutputStream();
    				output = new ObjectOutputStream(os);
    				
    				clients.add(output);
    				
                    output.flush();
                }
            } catch (IOException e) {
            	System.out.println("스트림 생성중 에러 발생");
            	e.printStackTrace();
            }

            try {
            	if (input != null){
            		Message inputmsg;
	                while ((inputmsg = (Message) input.readObject()) != null) {
	                    if (inputmsg != null) {
	                        switch (inputmsg.getType()) {
	                            case CHAT:
	                            	System.out.println("chat 전달받음");
	                            	boradCast(inputmsg);
	                                break;
	                            case REQUESTINIT:
	                            	System.out.println("[REQUESTINIT]");
	                            	if(currentVid!=null){
		                            	Message msg = new Message();
		                            	msg.setType(MessageType.INITVID);
		                            	msg.setVid(currentVid);
		                            	sendMessage(msg);
	                            	}
	                            	break;
	                            case VIDLENGTH:
	                            	if(isVidLengthSet()==false){
	                            		System.out.println("비디오 길이 전송받음, "+inputmsg.getMsg());
	                            		setCurrentVidLength(new Integer(inputmsg.getMsg()));
	                            	}
	                            	break;
	                            case INSERTVID:
	                            	System.out.println("[INSERTVID]" + inputmsg.getMsg());
	                            	VideoInfo vid = new VideoInfo();
	                            	vid.setVidId(inputmsg.getMsg());
	                            	vid.setUser(inputmsg.getUser());
	                            	vidQueue.add(vid);
	                            	break;
	                            case REQUESTQUEUELIST:
	                            	System.out.println("[REQUESTQUEUELIST]");
	                            	if(!vidQueue.isEmpty()){
		                            	Message msg = new Message();
		                            	msg.setType(MessageType.QUEUELIST);
		                            	msg.setQueue(vidQueue);
		                            	sendMessage(msg);
	                            	}
	                            	break;
	                            case REPORTERROR:
	                            	if(currentVid.getVidId().equals(inputmsg.getMsg()) && !currentVid.isVidLengthSet()){
	                            		System.out.println("[REPORTERROR]");
	                            		currentVid=null;
	                            	}
	                            	break;
	                            default :
	                        }
	                    }
	                }
            	}
            } catch (IOException | ClassNotFoundException e) {
                //report exception somewhere.
            	System.out.println("유저 로그아웃");
                //e.printStackTrace();
            }finally {
            	clients.remove(output);
            	closeStreams();
            	closeSocket();
            	System.out.println("[INFO]현재 접속자 수 : " + clients.size());
            }
        }    
    
        private void closeSocket() {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                	clientSocket.close();
                }
                System.out.println("cilent connection closed : " + this.toString());

            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        private void closeStreams(){
        	try{
        		if(input != null){
        			input.close();
        			System.out.println("인풋스트림 종료");
        		}
        		if(output != null){
        			output.close();
        			System.out.println("아웃풋스트림 종료");
        		}
        	} catch (IOException e){
        		e.printStackTrace();
        	}
        }
    }
}