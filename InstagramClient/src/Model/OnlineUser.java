package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OnlineUser
 * <p>
 * Version 1.0
 * <p>
 * Date:  11/30/2024
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE        AUTHOR        DESCRIPTION
 * --------------------------------------
 * 11/30/2024    NhanDinhVan    Create
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUser {
    public String ipAddress;
    public int port;
    public String email;
}
