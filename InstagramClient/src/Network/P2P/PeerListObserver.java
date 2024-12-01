package Network.P2P;

import Model.OnlineUser;

import java.util.Set;

/**
 * PeerListObserver
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


// Interface cho Observer, nơi sẽ nhận được cập nhật từ PeerList
public interface PeerListObserver {
    void update(Set<OnlineUser> updatedPeers); // Phương thức cập nhật khi danh sách peer thay đổi
}
