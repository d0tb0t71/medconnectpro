package com.example.medconnectpro;

public class DoctorModel {

    String  email, phone, fullname, username, department, city;

    public DoctorModel() {
    }

    public DoctorModel(String email, String phone, String fullname, String username, String department, String city) {
        this.email = email;
        this.phone = phone;
        this.fullname = fullname;
        this.username = username;
        this.department = department;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
