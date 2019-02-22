package com.autumn.app.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserToUpdate {

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

    private String newPassword;
    private String newPasswordConfirmed;

    public UserToUpdate(User currUser){
        if(currUser==null ) return;
        this.username = currUser.getUsername();
        this.email = currUser.getEmail();
        this.display_name = currUser.getDisplay_name();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPasswordConfirmed() {
        return newPasswordConfirmed;
    }

    public void setNewPasswordConfirmed(String newPasswordConfirmed) { this.newPasswordConfirmed = newPasswordConfirmed; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }



    public String getNewPassword() {
        return newPassword;
    }

    public String getDisplay_name() {
        return display_name;
    }

    @Override
    public String toString() {
        return "UserToUpdate{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", display_name='" + display_name + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", newPasswordConfirmed='" + newPasswordConfirmed + '\'' +
                '}';
    }
}
