package Network.P2P;

import Model.OnlineUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PeerList
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



public class PeerList {
    private static PeerList instance; // Singleton instance
    private final Set<OnlineUser> peers = new HashSet<>();
    private final List<PeerListObserver> observers = new ArrayList<>(); // Đối tượng lắng nghe thay đổi
    private final Lock lock = new ReentrantLock(); // Đảm bảo an toàn trong môi trường đa luồng

    // Constructor riêng tư để không thể tạo mới instance từ bên ngoài
    private PeerList() {}

    // Phương thức để lấy instance duy nhất của PeerList
    public static PeerList getInstance() {
        if (instance == null) {
            synchronized (PeerList.class) {
                if (instance == null) {
                    instance = new PeerList();
                }
            }
        }
        return instance;
    }

    // Thêm peer vào danh sách
    public void addPeer(OnlineUser user) {
        lock.lock();
        try {
            String peer = user.toString();
            if (!peers.contains(user)) {
                peers.add(user);
                notifyObservers(); // Thông báo thay đổi
            }
        } finally {
            lock.unlock();
        }
    }

    // Xóa peer khỏi danh sách
    public void removePeer(OnlineUser user) {
        lock.lock();
        try {
            String peer = user.toString();
            if (peers.contains(user)) {
                peers.remove(user);
                notifyObservers(); // Thông báo thay đổi
            }
        } finally {
            lock.unlock();
        }
    }

    // Lấy danh sách các peer hiện tại
    public Set<OnlineUser> getPeers() {
        return new HashSet<>(peers); // Trả về bản sao danh sách các peer
    }

    // Thêm Observer (người lắng nghe thay đổi)
    public void addObserver(PeerListObserver observer) {
        observers.add(observer);
    }

    // Xóa Observer
    public void removeObserver(PeerListObserver observer) {
        observers.remove(observer);
    }

    // Thông báo cho tất cả các Observer khi danh sách thay đổi
    private void notifyObservers() {
        for (PeerListObserver observer : observers) {
            observer.update(peers);
        }
    }
}
