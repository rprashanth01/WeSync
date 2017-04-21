package prashanth.wesync;

import android.app.Application;

import java.util.ArrayList;

import prashanth.wesync.models.ContactList;
import prashanth.wesync.models.UserInfo;

/**
 * Created by Abhi on 4/21/2017.
 */

public class GlobalClass extends Application {
    private ArrayList<ContactList> contactList;
    private ArrayList<UserInfo> contactListDB;

    private UserInfo currentUser;

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserInfo currentUser) {
        this.currentUser = currentUser;
    }

    public ArrayList<ContactList> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<ContactList> contactList) {
        this.contactList = contactList;
    }


    public ArrayList<UserInfo> getContactListDB() {
        return contactListDB;
    }

    public void setContactListDB(ArrayList<UserInfo> contactListDB) {
        this.contactListDB = contactListDB;
    }
}
