package com.example.medconnectpro;

public class UserDataManager {

    private static UserDataManager instance;
    private boolean isDoctor;
    private String email;
    private String department;
    private String city;

    private UserDataManager() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public synchronized void setIsDoctor(boolean userType) {
        this.isDoctor = userType;
    }

    public synchronized boolean isDoctor() {
        return isDoctor;
    }

    public synchronized void setEmail(String email) {
        this.email = email;
    }

    public synchronized String getEmail() {
        return email;
    }

    public synchronized void setDepartment(String department) {
        this.department = department;
    }

    public synchronized String getDepartment() {
        return department;
    }

    public synchronized void setCity(String city) {
        this.city = city;
    }

    public synchronized String getCity() {
        return city;
    }
}

