package com.example.lostandfound;

public class Data {
    public Data() {
    }
        public String key, username, itemName, desc, date, time, location, email, status, imageUrl;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }




    public Data(String username, String itemName, String desc, String date, String time, String location, String email, String status, String imageUrl) {
        this.username = username;
        this.itemName = itemName;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.location = location;
        this.email = email;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}


