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
        UserObject user2 = new UserObject("user2", "user2");
        UserObject Jinlin = new UserObject("Jinlin", "Jinlin");
        UserObject David = new UserObject("David", "David");
        UserObject Jack = new UserObject("Jack", "Jack");
        UserObject Ted = new UserObject("Ted", "Ted");
        userList.add(user1);
        userList.add(user2);
        userList.add(Jinlin);
        userList.add(David);
        userList.add(Jack);
        userList.add(Ted);
        user1.addFriend(new UserInfo(user2.getUserName()));
        user1.addFriend(new UserInfo(Jinlin.getUserName()));
        user1.addFriend(new UserInfo(Ted.getUserName()));
        user1.addFriend(new UserInfo(David.getUserName()));
        //user1.addFriend(new UserInfo(David.getUserName()));
        //user1.addBlock(new UserInfo(Jack.getUserName()));
        //user1.addBlock(new UserInfo(Ted.getUserName()));

        //user2.addFriend(new UserInfo(David.getUserName()));
        //user2.addFriend(new UserInfo(Jack.getUserName()));
        user2.addFriend(new UserInfo(user1.getUserName()));
        user2.addFriend(new UserInfo(David.getUserName()));
        //user2.addBlock(new UserInfo(Jinlin.getUserName()));
        //user2.addBlock(new UserInfo(Ted.getUserName()));

        //Ted.addFriend(new UserInfo(user2.getUserName()));
        //Ted.addFriend(new UserInfo(Jack.getUserName()));
        //Ted.addBlock(new UserInfo(Jinlin.getUserName()));
        Ted.addBlock(new UserInfo(user1.getUserName()));


        David.addFriend(new UserInfo(user1.getUserName()));
        David.addFriend(new UserInfo(user2.getUserName()));
        David.addBlock(new UserInfo(Jack.getUserName()));
        David.addBlock(new UserInfo(Ted.getUserName()));
    }

}
