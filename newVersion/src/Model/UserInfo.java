package Model;

/**
 * Created by bravado on 11/18/17.
 */
public class UserInfo {
    private String userName;
    private String ipAdress;

    public UserInfo(String userName, String ipAdress){

        this.userName = userName;
        this.ipAdress = ipAdress;
    }

    public UserInfo(String userName){
        this.userName = userName;
        ipAdress = "";
    }

    public String getUserName(){
        return userName;
    }

    public String getIpAdress(){
        return ipAdress;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setIpAdress(String ipAdress){
        this.ipAdress = ipAdress;
    }

}
