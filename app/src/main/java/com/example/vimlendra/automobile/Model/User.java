package com.example.vimlendra.automobile.Model;

public class User {

    private String Name,Password,phoneNumber,image, gender;

    private User(){

    }

    public User(String name, String password, String phoneNumber, String image, String gender) {
        Name = name;
        Password = password;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.gender = gender;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}


