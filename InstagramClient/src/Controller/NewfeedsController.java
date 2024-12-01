package Controller;

import Common.Constants;
import Model.Post;
import Network.ChatAction;
import Network.SuggestFollowersHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Common.ProfileService;
import Common.UserService;

public class NewfeedsController implements Initializable {
	private PostController postController = new PostController();

	private MessageController messageController = new MessageController();

	private SuggestedFollowerController suggestedFollowerController = new SuggestedFollowerController();

	private UserProfileController userProfileController = new UserProfileController();

	private ProfileService profileService;

	private Redirect redirect = new Redirect();

	@FXML
	private GridPane gridPane;

	@FXML
	private VBox vbox;

	@FXML
	private ScrollPane scroll;

	@FXML
	private Button message_btn;

	@FXML
	private Button create_btn;

	@FXML
	private Button personal_btn;

	private List<Post> posts = new ArrayList<>();

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			// load posts:
			postController.loadPosts(gridPane);

			// load suggested followers:
			suggestedFollowerController.loadSuggestFollower(vbox);

			// action listener message_btn:
			message_btn.setOnMouseClicked(arg0 -> {
				try {

					// gui tin hieu "message-view" den server yeu cau server cung cap list user:
//					Network.ChatAction chatAction = new ChatAction();
//					chatAction.requestChat();

					// show message-view.fxml:
					messageController.open_message_view(arg0, message_btn);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			// action listener create_btn:
			create_btn.setOnMouseClicked(e -> {
				postController.show_modal_create(e, create_btn);

			});

			// action open_profile:
			personal_btn.setOnMouseClicked(p -> {
				try {
					profileService = (ProfileService) Naming.lookup("rmi://"+ Constants.SERVER_ADDRESS +"/ProfileService");
					redirect.redirectPage("/View/UserProfile.fxml", personal_btn);
				} catch (IOException | NotBoundException e1) {
					e1.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
