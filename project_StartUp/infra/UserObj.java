package infra;

/**
 * Created by Allam on 2017/11/13.
 */
public class UserObj extends PacketObj {
    //unique
    String account;
    //changeable
    String userName;
    String password;

    public UserObj(String account, String userName, String password) {
        this.account = account;
        this.userName = userName;
        this.password = password;
    }

    public UserObj() {
        account = "";
        password = "";
        userName = "";
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String parse(){
        StringBuilder sb=new StringBuilder();

        if(account.length()==0)
            sb.append("nil");
        else
            sb.append(account);

        sb.append("#");

        if(userName.length()==0)
            sb.append("nil");
        else
            sb.append(userName);

        sb.append("#");

        if(password.length()==0)
            sb.append("nil");
        else
            sb.append(password);

        return sb.toString();
    }

    @Override
    public void deparse(String packet){

        String[] arr=packet.split("#");

        if(arr[0]=="nil")
            account=null;
        else
            account=arr[0];

        if(arr[1]=="nil")
            userName=null;
        else
            userName=arr[1];

        if(arr[2]=="nil")
            password=null;
        else
            password=arr[2];
    }

}
