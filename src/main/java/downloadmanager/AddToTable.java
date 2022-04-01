package downloadmanager;

import javafx.concurrent.Task;

public class AddToTable{

    private int no;
    private String name;
    public double status;
    private String size;
    private String added;
    public AddToTable(){
        no=0;
        name="";
        status=0.0;
        size="";
        added="";
    }

    public AddToTable(int no,String name,double status,String size,String added){
        this.no=no;
        this.name=name;
        this.status=status;
        this.size=size;
        this.added=added;
    }
    public int getNo(){
        return no;
    }
    public void setNo(int no){
        this.no=no;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public double getStatus(){
        return status;
    }
    public void setStatus(double status){
        this.status=status;
    }
    public String getSize(){
        return size;
    }
    public void setSize(String size){
        this.size=size;
    }
    public String getAdded(){
        return added;
    }
    public void setAdded(String added){
        this.added=added;
    }


}
