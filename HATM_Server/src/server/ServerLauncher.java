package server;

import java.net.SocketException;

public class ServerLauncher {

	public static void main(String[] args) {
		
		MultiThreadedServer server = new MultiThreadedServer(6767);
		try {
			server.run();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
