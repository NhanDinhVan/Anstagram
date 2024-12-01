package Common;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Notification
 * <p>
 * Version 1.0
 * <p>
 * Date:  12/1/2024
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE        AUTHOR        DESCRIPTION
 * --------------------------------------
 * 12/1/2024    NhanDinhVan    Create
 */


public class Notification {

    /**
     * Hiển thị thông báo nhanh trong 3 giây.
     * @param message Nội dung thông báo.
     */
    public static void showQuickNotification(String message) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // Làm cửa sổ trong suốt

            // Tạo nội dung thông báo
            Label label = new Label(message);
            label.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white; "
                    + "-fx-padding: 10px; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            label.setTextFill(Color.WHITE);

            StackPane root = new StackPane(label);
            root.setStyle("-fx-background-color: transparent;"); // Nền trong suốt

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // Nền cảnh cũng trong suốt
            stage.setScene(scene);

            // Định vị cửa sổ ở giữa màn hình
            stage.setWidth(300);
            stage.setHeight(50);
            stage.centerOnScreen();

            stage.show();

            // Tự động tắt sau 3 giây
            new javafx.animation.PauseTransition(Duration.seconds(3)).setOnFinished(event -> stage.close());
        });
    }
}