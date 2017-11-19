package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import client.loginview.LoginWindowController;
import client.mainview.MainWindowController;
import client.playlistview.PlayListWindowController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import message.*;

public class ClientManager {
	private MainWindowController controller;
	private PlayListWindowController playListController;
	
	private Socket socketConnection = null;
	private ObjectOutputStream output = null;
	private ObjectInputStream input = null;
	
	private boolean ready = false;
	
	private Thread socketReaderThread;
	private Thread setupThread;
	
	private Scene scene;
	private Stage playListStage;
	//private boolean isPlayListStageOn;
	
	private VideoInfo vid;
	private User user;

	public ClientManager(Scene scene) {
		this.scene = scene;
		//isPlayListStageOn = false;
		user = new User();
	}

	public Scene getScene() {
		return this.scene;
	}

	protected void initSocketConnection() throws SocketException {
		try {
			socketConnection = new Socket();
			/*
			 * Allows the socket to be bound even though a previous connection
			 * is in a timeout state.
			 */
			socketConnection.setReuseAddress(true);
			/*
			 * Create a socket connection to the server
			 */
			socketConnection.connect(new InetSocketAddress("localhost", 6767));
			System.out.println("접속완료");
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketException();
		}
	}

	/**
	 * Callback method invoked to notify that a user has been authenticated.
	 * Will show the main application screen.
	 */
	/*
	public void authenticated(String sessionID) {
		showMainView();
	}
	*/

	public void setUserName(String name) {
		user.setName(name);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void showLoginScreen() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("loginview/LoginWindow.fxml"));
			scene.setRoot((Parent) loader.load());
			LoginWindowController controller = loader.<LoginWindowController>getController();
			controller.initManager(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void closePlayListScreen() {
		playListStage.close();
	}

	public void popPlayListScreen() {
		Window window = scene.getWindow();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistview/PlayListWindow.fxml"));
			Scene playListScene = new Scene((Parent) loader.load());

			playListStage = new Stage();

			if (window instanceof Stage) {
				playListStage.initOwner((Stage) window);
			}

			playListStage.setScene(playListScene);
			playListStage.show();

			playListController = loader.<PlayListWindowController>getController();
			playListController.initController(this);

			Message msg = new Message();
			msg.setType(MessageType.REQUESTQUEUELIST);
			msg.setUser(user);
			sendMessage(msg);

			// playListController.setPlayListView(vidList);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void showMainView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("mainview/MainWindow.fxml"));
			scene.setRoot((Parent) loader.load());

			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			Window window = scene.getWindow();

			if (window instanceof Stage) {
				((Stage) window).setWidth(1834);
				((Stage) window).setHeight(914);
				((Stage) window).setX((bounds.getWidth() - 1834) / 2);
				((Stage) window).setY((bounds.getHeight() - 914) / 2);
			}

			controller = loader.<MainWindowController>getController();
			controller.initSessionID(this);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private synchronized void notifyReady() {
		ready = true;
		notifyAll();
	}

	private synchronized void waitForReady() {
		while (!ready) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public void sendMessage(Message msg) {
		try {
			System.out.println("메시지 서버로 전송" + msg.getMsg());
			output.writeObject(msg);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		try {
			/*
			 * Background thread to set up and open the input and output data
			 * streams.
			 */
			setupThread = new SetupThread();
			setupThread.start();
			/*
			 * Background thread to continuously read from the input stream.
			 */

			socketReaderThread = new SocketReaderThread();
			socketReaderThread.start();

			showMainView();

		} catch (Exception e) {
			System.out.println("[CLIENT]서버에 접속 도중 에러 발생");
			e.printStackTrace();
		}
	}

	private void close() {
		try {
			if (socketConnection != null && !socketConnection.isClosed()) {
				socketConnection.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void onMessage(Message msg) {
		javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	if (msg != null) {
        			switch (msg.getType()) {
        			case CHAT:
        				controller.appendTextToChatTextField(msg.getUser().getName() + " : " + msg.getMsg());
        				break;
        			case VIDID:
        				controller.receivedVid(msg.getVid().getVidId());
        				break;
        			case INITVID:
        				System.out.println(
        						"[서버로부터 INITVID 리시브]" + msg.getVid().getVidId() + ", 현재시간 : " + msg.getVid().getCurrentTime());
        				vid = msg.getVid();
        				controller.initPlayer(vid.getVidId(), vid.getCurrentTime());
        				break;
        			case QUEUELIST:
        				System.out.println("[서버로부터 QUEUELIST 리시브]");
        				playListController.setPlayListView(msg.getQueue());
        				break;
        			default:
        			}
        		}
            }
        });
	}

	class SocketReaderThread extends Thread {

		@Override
		public void run() {
			/*
			 * Wait until the socket is set up before beginning to read.
			 */
			waitForReady();

			/*
			 * Read from from input stream one line at a time
			 */
			try {
				if (input != null) {
					Message inputmsg;
					while ((inputmsg = (Message) input.readObject()) != null) {
						onMessage(inputmsg);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close();
			}
		}
	}

	class SetupThread extends Thread {

		@Override
		public void run() {
			try {
				initSocketConnection();
				if (socketConnection != null && !socketConnection.isClosed()) {
					/*
					 * Get input and output streams
					 */
					System.out.println("오브젝트 생성중");
					output = new ObjectOutputStream(socketConnection.getOutputStream());
					input = new ObjectInputStream(socketConnection.getInputStream());
					System.out.println("인풋오브젝트 생성 완료");
					System.out.println("오브젝트 생성 완료");
					output.flush();
				}
				/*
				 * Notify SocketReaderThread that it can now start.
				 */
				notifyReady();
			} catch (IOException e) {
				e.printStackTrace();
				/*
				 * This will notify the SocketReaderThread that it should exit.
				 */
				notifyReady();
			}
		}
	}
}