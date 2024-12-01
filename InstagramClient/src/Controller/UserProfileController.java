package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class UserProfileController {

	@FXML
	private ImageView avt;

	@FXML
	private Button customize_btn;

	@FXML
	private Label nicknameProfile;

	@FXML
	private Label numFollower;

	@FXML
	private Label numFollowing;

	@FXML
	private Label numPost;

	@FXML
	private Label usernameProfile;

	@FXML
	public void initialize() {
		initImageView();
	}

	private void initImageView() {
		// Tạo đối tượng Circle để cắt hình ảnh thành khuôn tròn
		avt.setFitHeight(220);
		avt.setFitWidth(220);
		
		Circle clip = new Circle();
        clip.setRadius(Math.min(avt.getFitWidth(), avt.getFitHeight()) / 2); // Đặt bán kính dựa trên kích thước
		clip.setCenterX(avt.getFitWidth() / 2); // Điều chỉnh vị trí tâm của Circle (giống kích thước ImageView)
		System.out.println("fit width: " + avt.getFitWidth());
		clip.setCenterY(avt.getFitHeight() / 2); // Điều chỉnh vị trí tâm của Circle (giống kích thước
													// ImageView)
		System.out.println("fit height: " + avt.getFitHeight());

		// Áp dụng Clip vào ImageView
		avt.setClip(clip);
	}
}
