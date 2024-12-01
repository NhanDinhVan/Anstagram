package View;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import Common.UserSearchService;
import Controller.MessageController;
import Controller.MessageItemController;
import Model.Partner;
import Model.User;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.stream.Collectors;

public class SearchPane {

	private Stage searchStage;

	public interface SearchCallback {
		void onUserSelected(Partner partner);
	}

	private SearchCallback searchCallback;

	// Danh sách gốc từ server
	private ObservableList<String> allUsers = FXCollections.observableArrayList();

	// Danh sách hiển thị dựa trên kết quả tìm kiếm
	private ObservableList<String> filteredUsers = FXCollections.observableArrayList();

	private UserSearchService userSearchService; // Dịch vụ tìm kiếm người dùng qua RMI

	private int selectedIndex = -1;

	// Thêm constructor để nhận callback
	public SearchPane(SearchCallback callback) {
		this.searchCallback = callback;
		try {
			Registry registry = LocateRegistry.getRegistry("localhost", 1099);
			userSearchService = (UserSearchService) registry.lookup("UserSearchService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Hàm mở cửa sổ tìm kiếm
	public void visibleSearchPane() {
		// Tạo Stage
		searchStage = new Stage();
		searchStage.initModality(Modality.APPLICATION_MODAL);
		searchStage.setTitle("Search");

		// Tạo TextField để tìm kiếm
		TextField searchField = new TextField();
		searchField.setPromptText("Tìm kiếm người dùng...");

		// Tạo ListView để hiển thị danh sách gợi ý
		ListView<String> suggestionList = new ListView<>();
		suggestionList.setItems(filteredUsers);
		suggestionList.setVisible(false); // Ẩn danh sách khi chưa có kết quả

		// Lắng nghe sự kiện nhập liệu
		searchField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			String query = searchField.getText().trim();
			if (!query.isEmpty()) {
				// Gửi yêu cầu tìm kiếm qua RMI tới server
				updateUserList(query);

				// Lọc danh sách người dùng theo từ khóa
				List<String> matchedUsers = allUsers.stream()
						.filter(user -> user.toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());

				filteredUsers.setAll(matchedUsers);
				suggestionList.setVisible(!filteredUsers.isEmpty());
			} else {
				suggestionList.setVisible(false);
			}
		});

		// Xử lý sự kiện di chuyển lên/xuống
		searchField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode().toString().equals("UP")) {
				// Di chuyển lên
				if (selectedIndex > 0) {
					selectedIndex--;
				}
				suggestionList.getSelectionModel().select(selectedIndex);
			} else if (event.getCode().toString().equals("DOWN")) {
				// Di chuyển xuống
				if (selectedIndex < filteredUsers.size() - 1) {
					selectedIndex++;
				}
				suggestionList.getSelectionModel().select(selectedIndex);
			} else if (event.getCode().toString().equals("ENTER")) {
				// Xử lý nhấn Enter
				String selectedUser = suggestionList.getSelectionModel().getSelectedItem();
				if (selectedUser != null) {

//					openChatWindow(selectedUser);
				}
			}
		});

		// Xử lý sự kiện chọn từ danh sách
		// Xử lý sự kiện khi người dùng chọn kết quả tìm kiếm
		suggestionList.setOnMouseClicked(event -> {
			String selectedUser = suggestionList.getSelectionModel().getSelectedItem();
			if (selectedUser != null) {
				searchField.setText(selectedUser);
				suggestionList.setVisible(false);

				// Gọi callback khi người dùng được chọn
				if (searchCallback != null) {
					Partner selectedPartner = findPartnerByUsername(selectedUser);

					if (selectedPartner != null) {
						searchCallback.onUserSelected(selectedPartner);
					} else {
						System.err.println("Không tìm thấy Partner cho người dùng: " + selectedUser);
					}
				}
			}
		});

		// Layout giao diện
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(15));
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(searchField, suggestionList);

		// Tạo Scene
		Scene scene = new Scene(layout, 300, 400);
		searchStage.setScene(scene);
		searchStage.showAndWait();
	}

	public Partner findPartnerByUsername(String username) {
		// Duyệt qua danh sách người dùng từ server
	    try {
	        List<User> serverUsers = userSearchService.searchUsers(); // Nếu cần, thêm phương thức này vào server
	        for (User user : serverUsers) {
	            if (user.getUsername().equals(username)) {
	                // Chuyển User thành Partner
	                return new Partner(user.getUserID(), user.getUsername(), user.getPassword(), user.getBio(), user.getEmail(), user.getProfile_picture(),
	            			user.getCreated_at(),user.getName()) ;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null; // Không tìm thấy
	}

	// Cập nhật danh sách người dùng từ server
	private void updateUserList(String query) {
		// Gọi phương thức tìm kiếm người dùng trên server thông qua RMI
		new Thread(() -> {
			try {
				List<User> serverUsers = userSearchService.searchUsers(); // Gọi hàm searchUsers từ server

				// Chuyển danh sách User thành danh sách tên người dùng (username) và cập nhật
				// lên giao diện
				List<String> usernames = serverUsers.stream().map(User::getUsername).collect(Collectors.toList());

				// Cập nhật danh sách người dùng trên luồng JavaFX
				Platform.runLater(() -> allUsers.setAll(usernames));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

}
