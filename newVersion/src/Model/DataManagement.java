package Model;

import java.util.*;

/**
 * Created by bravado on 11/18/17.
 */

public class DataManagement {
    public static final DataManagement INSTANCE = new DataManagement();

    private List<UserObject> userList;

    private Hashtable<String, UserObject> usersTable;
    //used to authenticate

    public DataManagement(){
        userList = new ArrayList<>();
        usersTable = new Hashtable<>();
        configuration();
    }

    public Hashtable<String, UserObject> getMap(){
        return usersTable;
    }

    public UserObject findUserByUserName(String name){
        for (UserObject object : userList){
            if (object.getUserName().equals(name)) return object;
        }
        return null;
    }


    private void configuration(){
        UserObject user = new UserObject("user1", "user1");
        UserInfo friend1 = new UserInfo("Bravado", "12.31.32.11");
        UserInfo friend2 = new UserInfo("David", "323.323.323.233");
        UserInfo friend3 = new UserInfo("Jack", "230.23.23.22");

        UserInfo block1 = new UserInfo("Jinlin", "12.3.2.3");
        UserInfo block2 = new UserInfo("Ted", "32.23.33.33");
        user.addFriend(friend1);
        user.addFriend(friend2);
        user.addFriend(friend3);
        user.addBlock(block1);
        user.addBlock(block2);

        UserObject user2 = new UserObject("user2", "user2");
        UserInfo friend4 = new UserInfo("Yinlin Li", "233.23.3.2");
        UserInfo friend5 = new UserInfo("Xiaoqin Zhu", "23.3.2.1");
        UserInfo block3 = new UserInfo("Yu Zheng", "23.2.23");
        UserInfo block4 = new UserInfo("Xian Li", "23.3.2.22");
        user2.addFriend(friend4);
        user2.addFriend(friend5);
        user2.addBlock(block3);
        user2.addBlock(block4);
        userList.add(user);
        userList.add(user2);
    }

}
