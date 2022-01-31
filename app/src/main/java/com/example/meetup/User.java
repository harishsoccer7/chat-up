package com.example.meetup;

import java.io.Serializable;

public class User implements Serializable {
    //Note : Firebase require your user defined object to have public variables or getter/setter.
    /** In java default access specifer is default which can't be accessible outside the package whereas public
    access specifier can be accessible outside the package also **/
    public String user_name,password;
    public boolean password_protected,session;
    User(){}// This empty constructor is for getting the value from the database
    User(String user_name,String password,Boolean password_protected,Boolean session){
        this.user_name = user_name;
        this.password = password;
        this.password_protected = password_protected;
        this.session = session;
    }//This constructor is for storing the values in the database because we send the value through the constructor only
}
