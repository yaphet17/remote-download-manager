package downloadmanager;

import httpserver.HttpServer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.*;
import java.time.LocalDate;

public class DownloadManagerController {
    private DownloadTableModel downloadTableModel;
    private URL url;
    public static String status="";
    @FXML
    private BorderPane pane;
    @FXML
    private Label statusLabel;
    @FXML
    private  Button downloadBtn;
    @FXML
    private Button resumeBtn;
    @FXML
    private Button pauseBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private  TextField urlField;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Download> allTable;
    @FXML
    private static TableView<Download> activeTable;
    @FXML
    private static TableView<Download> completedTable;
    @FXML
    private  TableColumn<Download,String> nameCol;
    @FXML
    private TableColumn<Download,Double> progressCol;
    @FXML
    private TableColumn<Download,Double> percentageCol;
    @FXML
    private TableColumn<Download,String> statusCol;
    @FXML
    private TableColumn<Download,String> sizeCol;
    @FXML
    private TableColumn<Download,String> addedCol;
    @FXML
    private  TableColumn<Download,String> nameACol;
    @FXML
    private TableColumn<Download,Double> progressACol;
    @FXML
    private TableColumn<Download,Double> percentageACol;
    @FXML
    private TableColumn<Download,String> statusACol;
    @FXML
    private TableColumn<Download,String> sizeACol;
    @FXML
    private TableColumn<Download,String> addedACol;
    @FXML
    private  TableColumn<Download,String> nameCCol;
    @FXML
    private TableColumn<Download,String> statusCCol;
    @FXML
    private TableColumn<Download,String> sizeCCol;
    @FXML
    private TableColumn<Download,String> addedCCol;




    @FXML
    private void initialize(){
        //Initialize table model
        downloadTableModel=new DownloadTableModel();
        //All table columns
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("contentSize"));
        progressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressCol.setCellFactory(ProgressBarTableCell.forTableColumn());
        percentageCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        addedCol.setCellValueFactory(new PropertyValueFactory<>("added"));
        //Active table columns
        nameACol.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeACol.setCellValueFactory(new PropertyValueFactory<>("contentSize"));
        progressACol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressACol.setCellFactory(ProgressBarTableCell.forTableColumn());
        percentageACol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        statusACol.setCellValueFactory(new PropertyValueFactory<>("message"));
        addedACol.setCellValueFactory(new PropertyValueFactory<>("added"));
        //Complete table columns
        nameCCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeCCol.setCellValueFactory(new PropertyValueFactory<>("contentSize"));
        statusCCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        addedCCol.setCellValueFactory(new PropertyValueFactory<>("added"));




        //Add key listener to main container
        pane.addEventFilter(KeyEvent.KEY_PRESSED,e->{
            //Listen Ctrl+V
            if(e.isControlDown()&&e.getCode()==KeyCode.V&&!urlField.isFocused()){
                //Grab copied value from the clipboard
                Clipboard clipboard=Clipboard.getSystemClipboard();
                if(clipboard.hasString()){
                    urlField.setText(clipboard.getString());
                }
            }
            //Fire downloadBtn when Enter key is pressed
            if(e.getCode()==KeyCode.ENTER&&!searchField.isFocused()){
                downloadBtn.fire();
            }
        });
        searchField.setOnKeyReleased(e->{
            if(!searchField.getText().equals("")){
                if(e.getCode().isLetterKey()||e.getCode().isDigitKey()||e.getCode()==KeyCode.SPACE||e.getCode()==KeyCode.BACK_SPACE) {
                    search(searchField.getText());
                }
            }else if(searchField.getText().equals("")&&downloadTableModel.getRowSize()>0&&!allTable.getItems().isEmpty()){
                restoreTable();
            }
        });
        String date= LocalDate.now().toString();
        //Set button actions
        downloadBtn.setOnAction(e->{
            newDownload(urlField.getText(),date);
        });
        //https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1280_10MG.mp4

        //Pause selected download
        pauseBtn.setOnAction(e->{
            if(!allTable.getSelectionModel().getSelectedCells().isEmpty()){
                allTable.getSelectionModel().getSelectedItem().Pause();
            }else{
                status="please select a download";
                statusLabel.setText(status);
            }

        });
        //Resume selected download
        resumeBtn.setOnAction(e->{
            if(!allTable.getSelectionModel().getSelectedCells().isEmpty()) {
                Download download = allTable.getSelectionModel().getSelectedItem();
                resumeDownload(download, date);
            }else{
                status="please select a download";
                statusLabel.setText(status);
            }
        });
        deleteBtn.setOnAction(e->{
            if(!allTable.getSelectionModel().getSelectedCells().isEmpty()) {
                int row = allTable.getSelectionModel().getSelectedCells().get(0).getRow();
                DownloadTableModel.removeDownload(row);
                allTable.getItems().remove(row);
            }else{
                status="please select a download";
                statusLabel.setText(status);
            }
        });

    }

    private void newDownload(String urlField,String date){
        //Today's date
        try {
            String urlString=urlField;
            if(verifyUrl(urlString)){
                url=new URL(urlField);
                //Throws IOException if connection is not available
                checkConnection(url);
                Download download=new Download(url);
                if(download.readyDownload()){
                    download.setName(download.getFileName());
                    download.setAdded(date);
                    downloadTableModel.addDownload(download);
                    allTable.getItems().add(download);
                    status="";
                    statusLabel.setText(status);
                }else{
                    statusLabel.setText(status);
                }
            }
        }catch (IOException ioException) {
            status="Remote host name is not found";
            statusLabel.setText(status);
        }
    }

    private void resumeDownload(Download download,String date){
        Download newDownload = new Download(download.getUrl());
        if (newDownload.readyDownload()) {
            newDownload.setName(download.getFileName());
            newDownload.setAdded(date);
            newDownload.downloaded=download.downloaded;
            DownloadTableModel.addDownload(newDownload);
            int row=allTable.getSelectionModel().getSelectedCells().get(0).getRow();
            allTable.getItems().remove(row);
            DownloadTableModel.removeDownload(row);
            allTable.getItems().add(row,newDownload);
        }
    }

    private boolean verifyUrl(String url){

        if(!url.startsWith("http://")){
            status="Invalid url format";
            statusLabel.setText(status);
            return false;
        }
        URL tempUrl;
        try {
            tempUrl=new URL(url);
        } catch (MalformedURLException e) {
            status="Invalid url format";
            statusLabel.setText(status);
            return false;
        }
        if(url.startsWith("https://www.youtube.com")){
            status="youtube download isn't allowed";
            statusLabel.setText(status);
            return false;
        }
        return tempUrl.getFile().length() >= 2;
    }
    private void search(String searchKey){
        allTable.getItems().clear();
        for(Download download:DownloadTableModel.downloadList){
            if(download.getName().startsWith(searchKey.strip())){
                allTable.getItems().add(download);
            }
        }
    }
    private void restoreTable(){
        allTable.getItems().clear();
        for(Download d:DownloadTableModel.downloadList){
            allTable.getItems().add(d);
        }
    }
    private void checkConnection(URL url) throws IOException {
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.connect();
        connection.disconnect();
    }

    public void startDownload(String url){
        urlField.setText(url);
        downloadBtn.fire();
    }

}


