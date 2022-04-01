package downloadmanager;

import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Download extends Task<Void>{

    //table columns
    public final IntegerProperty no;
    public final StringProperty name;
    public final StringProperty contentSize;
    public final StringProperty added;


    private final URL url;
    private HttpURLConnection connection;
    protected int downloaded;
    private int size;
    private int contentLength;
    private DownloadFlag status;

    private RandomAccessFile file;
    private InputStream input;

    public static final int MAX_BUFFER_SIZE=1024;

    private ArrayList<String> statuses;


    public Download(URL url){
        no=new SimpleIntegerProperty();
        name=new SimpleStringProperty();
        contentSize=new SimpleStringProperty();
        added=new SimpleStringProperty();
        this.url=url;

        file=null;
        input=null;

        //set the number of downloaded bytes to 0
        downloaded=0;
        //set the size of file to be downloaded to -1
        size=-1;
        contentLength=0;
        //set the status to downloading
        status=DownloadFlag.STARTING;
        statuses=new ArrayList<>();
        statuses.add("starting");
        statuses.add("downloading");
        statuses.add("paused");
        statuses.add("completed");
        statuses.add("canceled");
        statuses.add("error");
    }

    public URL getUrl() {
        return url;
    }

    public String getFileName() {
        String fileName=url.getFile();
        return fileName.substring(fileName.lastIndexOf("/")+1);
    }
    public int getSize() {
        return size;
    }
    public String getFileSize(){
        return String.format("%.2f MB",contentLength/1048576.0);
    }
    public DownloadFlag getStatus() {
        return status;
    }

    public float getProgress(int n) {
        if(size==-1){
            return 0;
        }
        return ((float)downloaded/size);
    }
    public void Pause() {
        status=DownloadFlag.PAUSED;
        updateMessage(statuses.get(status.getFlagNum()));
        connection.disconnect();
        DownloadTableModel.removeActiveDownload(this);
    }

    /*public void Resume() {
       System.out.println("resumed");
        updateMessage("downloading");
        downloadmanager.DownloadTableModel.addActiveDownload(this);
        downloadmanager.DownloadTableModel.restartDownload(this);
    }*/

    public void Cancel() {
        status=DownloadFlag.CANCELED;
        updateMessage(statuses.get(status.getFlagNum()));
        connection.disconnect();
        DownloadTableModel.removeActiveDownload(this);
    }
    public void Error() {
        status=DownloadFlag.ERROR;
        updateMessage("error");
        DownloadTableModel.removeActiveDownload(this);
        cancel();
    }

    public boolean readyDownload(){
        try {
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Range", "bytes"+downloaded+"-");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            if(connection.getResponseCode()/100!=2) {
                status=DownloadFlag.ERROR;
                //stateChanged();
            }
            //connect to server
            connection.connect();
            contentLength=connection.getContentLength();
            //Create file
            File tempFile=new File(getFileName());
            if(tempFile.length()==contentLength){
                DownloadManagerController.status="File already exist!";
                return  false;
            }
            file=new RandomAccessFile(tempFile,"rw");
            if(contentLength<1) {
                Error();
            }
            if(size==-1) {
                size=contentLength;
            }
            this.setContentSize(getFileSize());
        } catch (IOException e) {
            DownloadManagerController.status="Can't start download";
            return false;
        }
        status=DownloadFlag.DOWNLOADING;
        return true;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("Thread starting");
        updateMessage(statuses.get(status.getFlagNum()));
        updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS,100);
        try {
            input = connection.getInputStream();
            //skip downloaded bytes
            input.skip(downloaded);
            file.seek(downloaded);
           // while (status != downloadmanager.DownloadFlag.CANCELED || status != downloadmanager.DownloadFlag.ERROR){

                while (status == DownloadFlag.DOWNLOADING) {
                    //System.out.println("downloding");
                    byte[] buffer;
                    //set the size of the buffer according to remaining undownloaded bytes
                    if (size - downloaded > MAX_BUFFER_SIZE) {
                        buffer = new byte[MAX_BUFFER_SIZE];
                    } else {
                        buffer = new byte[size - downloaded];
                    }
                    int read = input.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    //write bytes to the file
                    file.write(buffer, 0, read);
                    //stateChanged();
                    downloaded += read;
                    updateMessage("downloading");
                    updateProgress(getProgress(0) * 100, 100);
                }
        //}
            if(status==DownloadFlag.DOWNLOADING) {
                status=DownloadFlag.COMPLETED;
                updateMessage("completed");
                updateProgress(1,1);
                DownloadTableModel.addActiveDownload(this);
                //stateChanged();
            }else{
                System.out.println("Thread stoped");
                return null;
            }
        } catch (Exception e) {
            Error();
            e.printStackTrace();
        }finally {
            if(file!=null) {
                try {
                    file.close();
                } catch (IOException e) {
                    Error();
                }
            }
            if(input!=null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Error();
                }
            }
        }
        System.out.println("Thread completed");
        connection.disconnect();
        return null;
    }
    //getters and setters
    private final IntegerProperty noProperty(){
        return no;
    }
    public final int getNo(){
        return no.get();
    }
    public final void setNo(int value){
        no.set(value);
    }
    private final StringProperty nameProperty(){
        return name;
    }
    public final String getName(){
        return name.get();
    }
    public final void setName(String value){
        name.set(value);
    }
    private final StringProperty contentSizeProperty(){
        return contentSize;
    }
    public final String getContentSize(){
        return contentSize.get();
    }
    public final void setContentSize(String value){
        contentSize.set(value);
    }
    private final StringProperty addedProperty(){
        return added;
    }
    public final String getAdded(){
        return added.get();
    }
    public final void setAdded(String value){
        added.set(value);
    }


}


