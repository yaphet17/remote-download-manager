package downloadmanager;

import httpserver.HttpServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DownloadManagerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(DownloadManagerApplication.class.getResource("downloadmanager-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 558);
        stage.setTitle("downloadmanager.Download Manager");
        stage.setScene(scene);
        stage.show();
        DownloadManagerController controller=fxmlLoader.getController();
//        //start http server
//        DownloadTableModel.startServer(new HttpServer(controller));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Thread t=new Thread(new HttpServer(controller));
                t.setDaemon(true);
                t.start();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}