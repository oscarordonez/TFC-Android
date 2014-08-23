package org.tfc.classes;

public class User{
    private String Obj_id;
    private String ACS_id;
    private String user;
    private String firstname;
    private String email;

    public User(String strObj_id, String strACS_id, String strUser, String strFirstName, String strEmail){
        this.Obj_id = strObj_id;
        this.ACS_id = strACS_id;
        this.user = strUser;
        this.firstname = strFirstName;
        this.email = strEmail;
    }

    public String getObj_id() {
        return Obj_id;
    }

    public String getACS_id() {
        return ACS_id;
    }

    public String getUser() {
        return user;
    }

    public String getFirstName() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }
}
