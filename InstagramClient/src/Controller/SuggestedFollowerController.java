package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Network.FollowAction;
import Network.LoginAction;
import Network.SuggestFollowersHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class SuggestedFollowerController {

	private SuggestFollowersHandler suggestFollowersHandler = new SuggestFollowersHandler();

	private FollowAction followAction = new FollowAction();

	@FXML
	private ImageView avtFollower;

	@FXML
	private Button followBtn;

	@FXML
	private Label fullnameFollower;

	@FXML
	private Label suggestedFollower;

	private JSONObject suggest;

	@FXML
	public void initialize() {
		
		initImageView();
		
		followBtn.setOnMouseClicked(event -> {
			System.out.println("follow...");
			submitFollow(event);
		});
	}

	// click "follow":
	public void submitFollow(MouseEvent event) { // follow hoặc unfollow:
		// send id followed cho server:
		followAction.transferFollowedID(LoginAction.userID.intValue(), getFollowedID().intValue(), followBtn.getText(),
				followBtn);

	}

	// Phương thức khởi tạo clip cho ImageView
	private void initImageView() {
		// Tạo đối tượng Circle để cắt hình ảnh thành khuôn tròn
		Circle clip = new Circle();
		clip.setRadius(15); // Điều chỉnh bán kính theo kích thước hình ảnh bạn muốn
		clip.setCenterX(avtFollower.getFitWidth() / 2); // Điều chỉnh vị trí tâm của Circle (giống kích thước ImageView)
		clip.setCenterY(avtFollower.getFitHeight() / 2); // Điều chỉnh vị trí tâm của Circle (giống kích thước
															// ImageView)

		// Áp dụng Clip vào ImageView
		avtFollower.setClip(clip);
	}

	public Long getFollowedID() {
		return (Long) this.suggest.get("suggestedID");
	}

	public void setItemFollower(JSONObject suggest) {
		this.suggest = suggest;
		fullnameFollower.setText((String) suggest.get("suggestedUsername"));
		suggestedFollower.setText((String) suggest.get("suggestedName"));
		Image image = new Image(getClass().getResource("/View/" + suggest.get("suggestedAvt")).toExternalForm());
		avtFollower.setImage(image);

	}

	// load suggest follows :
	public void loadSuggestFollower(VBox vbox) throws IOException {
		Label label = new Label("Gợi ý cho bạn");
		vbox.getChildren().add(label);

		JSONArray suggestions = suggestFollowersHandler.requestSuggestFollow(LoginAction.userID.intValue());
		if (suggestions != null) {
			for (int i = 0; i < suggestions.size(); i++) {
				// get mỗi jsonObject từ jsonArray:
				JSONObject suggestJSON = (JSONObject) suggestions.get(i);

				// Hiển thị component suggest-follower:
				URL resource = getClass().getResource("/View/suggested-follower-items.fxml");
				if (resource == null) {
					System.err.println("FXML file not found!");
				}
				FXMLLoader fxmlLoader = new FXMLLoader(resource);
				fxmlLoader.setLocation(resource);
				AnchorPane anchorPane2 = fxmlLoader.load();

				SuggestedFollowerController suggestedFollowerController = fxmlLoader.getController();

				if (suggestedFollowerController != null) {
					suggestedFollowerController.setItemFollower(suggestJSON);
				} else {
					System.out.println("suggestedFollowerController is null");
				}
				vbox.getChildren().add(anchorPane2);
			}
		} else {
			System.out.println("Suggestions is null !");
		}

	}

}
