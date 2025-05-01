package com.example.lostandfound;



public class User {
    public String email, username, stId;

//    public String getStId() {
//        return stId;
//    }
//
//    public void setStId(String stId) {
//        this.stId = stId;
//    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }



    public User( String email, String username) {

        this.email = email;
        this.username = username;


    }

    public User() {
    }
}

