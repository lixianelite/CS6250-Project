package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bravado on 11/18/17.
 */
public class UserObject extends PacketObject {

    private String userName;
    private String password;
    private List<UserInfo> friendList;
    private List<UserInfo> blockList;

    public UserObject(String userName, String password) {
        this.userName = userName;
        this.password = password;
        friendList = new ArrayList<>();
        blockList = new ArrayList<>();
    }

    public UserObject(){
        userName = "";
        password = "";
        friendList = new ArrayList<>();
        blockList = new ArrayList<>();
    }

    public void addFriend(UserInfo friend){
        for (int i = 0; i < friendList.size(); i++){
            String userName = friendList.get(i).getUserName();
            if (friend.getUserName().equals(userName)) return;
        }
        friendList.add(friend);
    }

    public void addBlock(UserInfo user){
        for (int i = 0; i < blockList.size(); i++){
            String userName = blockList.get(i).getUserName();
            if (user.getUserName().equals(userName)) return;
        }
        blockList.add(user);
    }

    public List<UserInfo> getFriendList(){
        return friendList;
    }

    public List<UserInfo> getBlockList(){
        return blockList;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public String parse(){
        StringBuilder sb = new StringBuilder();

        if(userName.length()==0) {
            sb.append("nil");
        } else{
            sb.append(userName);
        }
        sb.append("#");
        if(password.length()==0){
            sb.append("nil");
        } else{
            sb.append(password);
        }

        return sb.toString();
    }

    @Override
    public void deParse(String packet){

        String[] arr=packet.split("#");

        if(arr[0]=="nil"){
            userName=null;
        } else{
            userName=arr[0];
        }

        if(arr[1]=="nil"){
            password=null;
        } else{
            password=arr[1];
        }
    }

}