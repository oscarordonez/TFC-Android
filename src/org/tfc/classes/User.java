package org.tfc.classes;

public class User{

    private String ACS_id;
    private String user;
    private String firstname;
    private String email;

    public User(String StrACS_id, String StrUser, String StrFirstName, String StrEmail){
        this.ACS_id = StrACS_id;
        this.user = StrUser;
        this.firstname = StrFirstName;
        this.email = StrEmail;
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
