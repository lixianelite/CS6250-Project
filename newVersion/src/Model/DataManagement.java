package Model;

import java.util.*;

/**
 * Created by bravado on 11/18/17.
 */

public class DataManagement {
    public static final DataManagement INSTANCE = new DataManagement();

    private List<UserObject> userList;


    public DataManagement(){
        userList = new ArrayList<>();
        configuration();
    }

    public UserObject findUserByUserName(String name){
        for (UserObject object : userList){
            if (object.getUserName().equals(name)) return object;
        }
        return null;
    }

    public void addUser(UserObject user){
        userList.add(user);
    }

    public void printUserList(){
        for (int i = 0; i < userList.size(); i++){
            System.out.print(userList.get(i).getUserName() + " ");
        }
        System.out.println();
    }

    private void configuration(){
        UserObject user1 = new UserObject("user1", "user1");
        UserInfo friend1 = new UserInfo("Bravado");
        UserInfo friend2 = new UserInfo("David");
        UserInfo friend3 = new UserInfo("Jack");
        UserInfo u2 = new UserInfo("user2");
        UserInfo u1 = new UserInfo("user1");


        UserInfo block1 = new UserInfo("Jinlin");
        UserInfo block2 = new UserInfo("Ted");
        user1.addFriend(friend1);
        user1.addFriend(friend2);
        user1.addFriend(friend3);
        user1.addFriend(u2);
        user1.addBlock(block1);
        user1.addBlock(block2);

        UserObject user2 = new UserObject("user2", "user2");
        UserInfo friend4 = new UserInfo("Yinlin Li");
        UserInfo friend5 = new UserInfo("Xiaoqin Zhu");
        UserInfo block3 = new UserInfo("Yu Zheng");
        UserInfo block4 = new UserInfo("Xian Li");
        user2.addFriend(friend4);
        user2.addFriend(friend5);
        user2.addFriend(u1);
        user2.addBlock(block3);
        user2.addBlock(block4);

        userList.add(user1);
        userList.add(user2);
    }

}
