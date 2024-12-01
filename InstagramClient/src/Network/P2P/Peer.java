package Network.P2P;

import Common.Notification;
import Model.OnlineUser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Set;

/**
 * Peer
 * <p>
 * Version 1.0
 * <p>
 * Date:  11/29/2024
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE        AUTHOR        DESCRIPTION
 * --------------------------------------
 * 11/29/2024    NhanDinhVan    Create
 */

public class Peer implements Runnable {
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final int MULTICAST_PORT = 9876;
    private static final int PRIVATE_PORT = 5000;
    private DatagramSocket privateSocket; // Thêm socket riêng tư vào class
    public static String IP_ADDRESS;
    public static String USER_NAME;

    private MulticastSocket multicastSocket;
    private InetAddress group;

    public Peer(String username) throws IOException {
        group = InetAddress.getByName(MULTICAST_ADDRESS);
        multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocket.joinGroup(group);
        privateSocket = new DatagramSocket(PRIVATE_PORT); // Khởi tạo socket riêng tư
        IP_ADDRESS = getLocalIPAddress();
        USER_NAME = username;

        // Thêm shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }


    public void start() {
        // Lấy instance của PeerList
        PeerList peerList = PeerList.getInstance();

        // Đăng ký observer (chỗ này có thể thêm các đối tượng cần lắng nghe thay đổi)
        peerList.addObserver(new PeerListObserver() {
            @Override
            public void update(Set<OnlineUser> updatedPeers) {
                System.out.println("Danh sách peer đã thay đổi:");
                for (OnlineUser peer : updatedPeers) {
                    System.out.println(peer.toString());
                }
            }
        });

        // Bắt đầu lắng nghe multicast
        new Thread(this).start();

        // Phát thông tin multicast
        startMulticastSender();

        // Bắt đầu lắng nghe tin nhắn riêng tư
        startPrivateListener();
    }

    private void startMulticastSender() {
        new Thread(() -> {
            try (MulticastSocket sendSocket = new MulticastSocket()) {
                sendSocket.setTimeToLive(5);
                if (IP_ADDRESS == null) {
                    System.err.println("Không thể lấy địa chỉ IP.");
                    return;
                }

                // Tạo thông điệp JSON
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("ip", IP_ADDRESS);
                jsonMessage.put("port", PRIVATE_PORT);
                jsonMessage.put("email", USER_NAME);
                jsonMessage.put("signal", "new"); // Thêm signal = "new" khi peer mới tham gia
                String message = jsonMessage.toString();
                byte[] sendData = message.getBytes();

                System.out.println("Phát thông tin multicast: " + message);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, group, MULTICAST_PORT);
                    sendSocket.send(packet);
                    Thread.sleep(3000); // Phát thông tin mỗi 3 giây
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void cleanup() {
        try {
            // Gửi tín hiệu exit
            JSONObject exitMessage = new JSONObject();
            exitMessage.put("ip", IP_ADDRESS);
            exitMessage.put("port", PRIVATE_PORT);
            exitMessage.put("email", USER_NAME);
            exitMessage.put("signal", "exit");
            byte[] exitData = exitMessage.toString().getBytes();

            DatagramPacket exitPacket = new DatagramPacket(exitData, exitData.length, group, MULTICAST_PORT);
            multicastSocket.send(exitPacket);

            System.out.println("Gửi tín hiệu exit: " + exitMessage);

            // Đóng các socket
            if (privateSocket != null && !privateSocket.isClosed()) {
                privateSocket.close();
            }

            if (multicastSocket != null && !multicastSocket.isClosed()) {
                multicastSocket.leaveGroup(group);
                multicastSocket.close();
            }

            System.out.println("Đã đóng các cổng và tài nguyên.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startPrivateListener() {
        new Thread(() -> {
            try {
                System.out.println("Đang lắng nghe tin nhắn riêng tư trên cổng " + PRIVATE_PORT);
                while (!privateSocket.isClosed()) { // Kiểm tra nếu socket chưa đóng
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    privateSocket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress senderAddress = packet.getAddress();
                    int senderPort = packet.getPort();

                    Notification.showQuickNotification("Nhận tin nhắn riêng tư từ " + senderAddress + ":" + senderPort + " - Nội dung: " + message);
                    System.out.println("Nhận tin nhắn riêng tư từ " + senderAddress + ":" + senderPort + " - Nội dung: " + message);
                }
            } catch (SocketException e) {
                System.out.println("Socket đã đóng: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Đang lắng nghe thông tin multicast...");
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Nhận thông tin multicast từ peer khác: " + message);

                // Chuyển đổi chuỗi nhận được thành JSONObject
                JSONObject receivedJson = new JSONObject(message);
                String signal = receivedJson.optString("signal", "");

                PeerList peerList = PeerList.getInstance();

                if ("new".equals(signal)) {
                    // Nếu là signal "new", thêm peer vào danh sách
                    String ip = receivedJson.getString("ip");
                    int port = receivedJson.getInt("port");
                    String email = receivedJson.getString("email");

                    OnlineUser user = new OnlineUser(ip, port, email);

                    peerList.addPeer(user);
                } else if ("exit".equals(signal)) {
                    // Nếu là signal "exit", xóa peer khỏi danh sách
                    String ip = receivedJson.getString("ip");
                    int port = receivedJson.getInt("port");
                    String email = receivedJson.getString("email");

                    OnlineUser user = new OnlineUser(ip, port, email);

                    peerList.removePeer(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback()) continue;

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static void setIpAddress(String ipAddress) {
        IP_ADDRESS = ipAddress;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public static void setUserName(String userName) {
        USER_NAME = userName;
    }
}
