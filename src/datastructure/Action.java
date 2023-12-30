package datastructure;

import admin.ActionMagament;

public class Action {
    private int ID;
    private String fullname;
    private int login;
    private int userChat;
    private int groupChat;
    public Action(){
        this.ID = -1;
        this.fullname = "";
        this.login = 0;
        this.userChat = 0;
        this.groupChat = 0;
    }
    public Action(int ID, String fullname, int login, int userChat, int groupChat){
        this.ID = ID;
        this.fullname = fullname;
        this.login = login;
        this.userChat = userChat;
        this.groupChat = groupChat;
    }
    public void setID(int ID){this.ID = ID;}
    public int getID(){return this.ID;}
    public void setFullName(String fullname){this.fullname = fullname;}
    public String getFullName(){return this.fullname;}
    public void setLogin(int login){this.login = login;}
    public int getLogin(){return this.login;}
    public void setUserChat(int userChat){this.userChat = userChat;}
    public int getUserChat(){return this.userChat;}
    public void setGroupChat(int groupChat){this.groupChat = groupChat;}
    public int getGroupChat(){return this.groupChat;}
}
