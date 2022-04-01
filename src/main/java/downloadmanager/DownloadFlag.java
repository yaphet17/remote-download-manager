package downloadmanager;

public enum DownloadFlag {
    STARTING(0),
    DOWNLOADING(1),
    PAUSED(2),
    COMPLETED(3),
    CANCELED(4),
    ERROR(5);

    int flagNum;
    DownloadFlag(int flagNum){
        this.flagNum=flagNum;
    }
    public int getFlagNum(){
        return flagNum;
    }
}
