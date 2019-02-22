package com.autumn.app.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;

public class User {
        //@NotNull(message = "Username cannot be empty")
        @Size(min = 2, message = "Username must be longer")
        @Size(max = 20, message = "Username should be shorter")
    private String username;
        //@NotNull(message = "Email cannot be empty")
        @Size(min = 2, message = "Email must be longer")
        @Email(message = "Email must be a valid email address")
    private String email;
        //@NotNull(message = "Password cannot be empty")
        @Size(min = 2, message = "Password must be longer")
    private String password;
        //@NotNull(message = "Name cannot be empty")
        @Size(min = 2, message = "Name must be longer")
        @Size(max = 35, message = "Name should be shorter")
    private String display_name;
    private boolean enabled;
    private ArrayList<Vote> voteArrList =new ArrayList<Vote>();
    private String rol;

    public User(){}

    public User(String username, String email, String password, String display_name, boolean enabled, String rol)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.display_name = display_name;
        this.enabled = enabled;
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public ArrayList<Vote> getVoteArrList() {
        return voteArrList;
    }

    public void setVoteArrList(ArrayList<Vote> voteArrList) {
        this.voteArrList = voteArrList;
    }



     public void voteANote(Vote vote) {
        voteArrList.add(vote);

    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRol()
    {
        return this.rol;
    }
    public void setRol(String rol)
    {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof User)
            return ((User) obj).getUsername().equals(this.username);

        return false;
    }

    public static class UserBuilder{
        String username = "";
        String email = "";
        String password = "";
        String display_name = "";
        boolean enabled = false;
        String rol = "";

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder displayName(String display_name) {
            this.display_name = display_name;
            return this;
        }

        public UserBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserBuilder rol(String rol) {
            this.rol = rol;
            return this;
        }

        public User build(){
            return new User(username, email, password, display_name, enabled, rol);
        }
    }

}
