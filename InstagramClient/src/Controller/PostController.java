package Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Model.Post;
import Network.PostHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PostController {
	private PostHandler postHandler = new PostHandler();

	private String filePath;

	private List<Post> posts = new ArrayList<>();

	public List<Post> getListPost() {
		List<Post> listPosts = new ArrayList<>();
		try {

			for (int i = 0; i < 5; i++) {
				Post post = new Post();
				post.setPoster("tungocng.07");
				post.setTime("Hôm nay");
				post.setContentPost("Furniture project ........");
				post.setAmountFavorite("20 lượt thích");
//                post.setImageSrc("com/example/desktopapp/6e70cf15912f7d4da213cddc6aedccad.jpg");
				listPosts.add(post);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listPosts;

	}

	public void loadPosts(GridPane gridPane) throws IOException {
		posts.addAll(getListPost());
		int row = 0;

		// load posts:
		for (int i = 0; i < posts.size(); i++) {
			URL resource = getClass().getResource("/View/post-item.fxml");
			if (resource == null) {
				System.err.println("FXML file not found!");
			}
			FXMLLoader fxmlLoader = new FXMLLoader(resource);
			fxmlLoader.setLocation(resource);

			AnchorPane anchorPane = fxmlLoader.load();

			PostItemController postItemController = fxmlLoader.getController();
			if (postItemController != null) {
				postItemController.setDataPost(posts.get(i));
			} else {
				System.out.println("postItemController is null");
			}

			gridPane.add(anchorPane, 1, row++);
			GridPane.setMargin(anchorPane, new Insets(10));
		}
	}

	// khi click createBtn trong newfeed-view.fxml :
	public void show_modal_create(MouseEvent event, Button create_btn) {
		try {
			// Tạo một cửa sổ mới
			Stage modal = new Stage();
			modal.initModality(Modality.WINDOW_MODAL); // Đặt cửa sổ này là modal
			modal.initOwner(create_btn.getScene().getWindow()); // Modal phụ thuộc vào cửa sổ chính

			// Tạo label cho tiêu đề
			Label titleLabel = new Label("Create new post");

			// Tạo khu vực kéo/thả hình ảnh hoặc video
			// Tạo khu vực kéo/thả hình ảnh hoặc video
			VBox dropArea = new VBox(); // Sử dụng VBox để chứa hình ảnh hoặc video
			dropArea.setPrefSize(300, 200);
			dropArea.setStyle("-fx-background-color: lightgray; -fx-border-radius: 10; -fx-background-radius: 10;");

			// Thêm biểu tượng hình ảnh và video
			ImageView icon = new ImageView(new Image("https://example.com/icon.png"));
			icon.setFitWidth(50);
			icon.setFitHeight(50);

			// Label chỉ dẫn
			Label instructionLabel = new Label("Drag photos and videos here");

			// Nút chọn file từ máy tính
			Button selectFileButton = new Button("Select from computer");

			// Nút NEXT để chuyển đến Scene nhập nội dung bài đăng
			Button nextButton = new Button("NEXT");
			nextButton.setOnAction(n -> showContentInputScene(modal, dropArea));

			// Sắp xếp các phần tử trong VBox
			VBox modalLayout = new VBox(10, titleLabel, dropArea, instructionLabel, selectFileButton, nextButton);
			modalLayout.setAlignment(Pos.CENTER);

			// Tạo Scene cho modal và hiển thị
			Scene modalScene = new Scene(modalLayout, 400, 400);
			modal.setScene(modalScene);
			modal.show();

			// Khi nhấn nút Create, xử lý sự kiện và đóng cửa sổ modal
			selectFileButton.setOnAction(e -> {
				handleVideo_Image(dropArea, modal);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleVideo_Image(VBox dropArea, Stage modal) {
		// Mở FileChooser khi nhấn nút
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Photo or Video");

		// Lọc chỉ chọn hình ảnh và video
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
				new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov"));

		// Hiển thị hộp thoại chọn tệp và lấy tệp đã chọn
		File selectedFile = fileChooser.showOpenDialog(modal);
		if (selectedFile != null) {
			filePath = selectedFile.toURI().toString(); // filepath = file:/C:/Users/Admin/Downloads/vkulogo.png

			// kiểm tra loại tệp và hiển thị nội dung:
			if (filePath.endsWith(".png") || filePath.endsWith(".jpg")) {
				// Nếu là hình ảnh:
				ImageView imageView = new ImageView(new Image(filePath));
				imageView.setFitHeight(300);
				imageView.setFitWidth(400);
				imageView.setPreserveRatio(true);

				// Xóa nội dung cũ và thêm hình ảnh vào dropArea
				dropArea.getChildren().clear();
				dropArea.getChildren().add(imageView);

			} else if (filePath.endsWith(".mp4") || filePath.endsWith(".avi") || filePath.endsWith(".mov")) {
				// Nếu là video:
				Media media = new Media(filePath);
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				MediaView mediaView = new MediaView(mediaPlayer);
				mediaView.setFitWidth(300);
				mediaView.setFitHeight(200);
				mediaView.setPreserveRatio(true);

				// Xóa nội dung cũ và thêm video vào dropArea
				dropArea.getChildren().clear();
				dropArea.getChildren().add(mediaView);

				// Phát video
				mediaPlayer.setAutoPlay(true);
			}
		} else {
			System.out.println("No file selected");
		}

	}

	public void showContentInputScene(Stage modal, VBox dropArea) {
		// Tạo VBox để chứa nội dung của modal mới
		VBox contentLayout = new VBox(10);
		contentLayout.setPadding(new Insets(10));

		// Tạo thanh tiêu đề với nút trở về và nút Share
		Label titleLabel = new Label("Create new post");

//		Button closeButton = new Button("X"); // Nút quay lại
//		closeButton.setOnAction(e -> modal.close());

		Button shareButton = new Button("Share");
		shareButton.setDisable(false);

		// Đặt tiêu đề và nút Share trong HBox
		HBox titleBar = new HBox(10, titleLabel, shareButton);
		titleBar.setAlignment(Pos.CENTER_LEFT);
		titleBar.setSpacing(200); // Để căn các thành phần trong titleBar

		// ImageView hoặc MediaView hiển thị ảnh/video đã chọn từ dropArea
		ImageView postImageView = new ImageView();
		postImageView.setFitWidth(300);
		postImageView.setFitHeight(300);

		// Thêm ảnh đã chọn từ giao diện trước vào postImageView
		if (!dropArea.getChildren().isEmpty() && dropArea.getChildren().get(0) instanceof ImageView) {
			postImageView.setImage(((ImageView) dropArea.getChildren().get(0)).getImage());
		}

		// TextArea để nhập nội dung bài đăng
		TextArea contentArea = new TextArea();
		contentArea.setPromptText("Write a caption...");
		contentArea.setPrefHeight(100);

		// Các tùy chọn khác như "Add location", "Add collaborators"
		TextField locationField = new TextField();
		locationField.setPromptText("Add location");

		TextField collaboratorField = new TextField();
		collaboratorField.setPromptText("Add collaborators");

		shareButton.setOnMouseClicked(a -> {
			submitPostContentEvent(a, contentArea, modal); // Gửi bài đăng khi nhấn "Share"
		});

		// Sắp xếp các thành phần vào layout chính
		contentLayout.getChildren().addAll(titleBar, postImageView, contentArea, locationField, collaboratorField);

		// Tạo Scene cho cửa sổ mới và hiển thị
		Scene contentScene = new Scene(contentLayout, 400, 600);
		modal.setScene(contentScene);

	}

	public void submitPostContentEvent(MouseEvent event, TextArea contentArea, Stage modal) {
		System.out.println("clicking....");
		String postCaption = contentArea.getText();
		if (!postCaption.equals("")) {
			postHandler.sendImage_Video(getFilePath(), postCaption);
		}

	}

	public String getFilePath() {
		String path = filePath.substring(6); // cắt bỏ 6 kí 6 kí tự đầu tiên "file:/"
		return path;

	}

}
