package Controller;

import Model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class PostItemController {
	@FXML
	private javafx.scene.image.ImageView avatar;

	@FXML
	private ImageView commentBtn;

	@FXML
	private Text contents;

	@FXML
	private javafx.scene.control.Label countLike;

	@FXML
	private Label date;

	@FXML
	private javafx.scene.image.ImageView imgPost;

	@FXML
	private javafx.scene.image.ImageView shareBtn;

	@FXML
	private javafx.scene.image.ImageView tymBtn;

	@FXML
	private javafx.scene.control.Label loader;
	private Post post;

	public void setDataPost(Post post) {
		countLike.setText(post.getAmountFavorite());
		date.setText(post.getTime());
		loader.setText(post.getPoster());

	}

}
