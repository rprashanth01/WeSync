package prashanth.wesync.models;

/**
 * Created by Abhi on 4/21/2017.
 */

public class MessageModel {

    private String msg;
    private String user;

    public MessageModel() {
    }

    public MessageModel(String msg, String user) {
        this.msg = msg;
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
