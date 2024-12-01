//package Network;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.util.List;
//
//import Common.UserSearchService;
//import Model.User;
//
//public class SearchAction {
//
//	// request đến server yêu cầu server cung cấp listUser
//	public void requestSearching() {
//		try {
//
//			// Kết nối tới RMI Registry
//			Registry registry = LocateRegistry.getRegistry("localhost", 1099);
//
//			// Lấy đối tượng UserSearchService từ registry
//			UserSearchService userSearchService = (UserSearchService) registry.lookup("UserSearchService");
//			
//			// Tìm kiếm:
//			List<User> users = userSearchService.searchUsers("");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
