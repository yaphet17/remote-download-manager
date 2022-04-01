package downloadmanager;

import httpserver.HttpServer;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DownloadTableModel {
    public static ArrayList<Download> downloadList;
    public static ArrayList<Download> activeDownloadList;
    public static ArrayList<Download> completedDownloadList;
    private static Executor executor;
    public DownloadTableModel(){

        downloadList= new ArrayList<>();
        activeDownloadList=new ArrayList<>();
        completedDownloadList=new ArrayList<>();
        executor=Executors.newFixedThreadPool(10, r -> {
            Thread t=new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    public static void addDownload(Download download){
        downloadList.add(download);
        activeDownloadList.add(download);
        executor.execute(download);
    }
    public static void startServer(HttpServer httpServer){
        executor.execute(httpServer);
    }


    public static void addActiveDownload(Download download){
        activeDownloadList.add(download);
    }
    public static void removeActiveDownload(Download download){
        activeDownloadList.remove(download);
    }
    public static void addCompletedDownload(Download download){
        completedDownloadList.add(download);
    }
    public static void restartDownload(Download download) {

    }
    public static void removeDownload(int row){
        downloadList.remove(row).Cancel();
    }

    public int getRowSize(){
        return downloadList.size();
    }




}
