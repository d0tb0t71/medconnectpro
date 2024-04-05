package com.example.medconnectpro;

public class AppointmentModel {

    String id, name, mobile, date, time, bGroup, city, bookedBy, docName , docDep;

    public AppointmentModel() {
    }

    public AppointmentModel(String id, String name, String mobile, String date, String time, String bGroup, String city, String bookedBy, String docName, String docDep) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.date = date;
        this.time = time;
        this.bGroup = bGroup;
        this.city = city;
        this.bookedBy = bookedBy;
        this.docName = docName;
        this.docDep = docDep;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getbGroup() {
        return bGroup;
    }

    public void setbGroup(String bGroup) {
        this.bGroup = bGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocDep() {
        return docDep;
    }

    public void setDocDep(String docDep) {
        this.docDep = docDep;
    }
}
