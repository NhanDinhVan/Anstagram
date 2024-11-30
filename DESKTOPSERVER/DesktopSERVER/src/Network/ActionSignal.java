package Network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ActionSignal {

	public void listenFromClient(DatagramSocket serverSocket) {
		try {
			while (true) {
				byte data[] = new byte[1024];
				DatagramPacket dataPacket = new DatagramPacket(data, data.length);
				serverSocket.receive(dataPacket);

				InetAddress inetAddr = dataPacket.getAddress();
				int port = dataPacket.getPort();
				new AnalyseAction().analyseAction(new String(dataPacket.getData()), port, inetAddr, serverSocket);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
