package com.itel.app.recycleion1;

public class ItemData {

    private String status;
    private String schedule_time;
    private int imageUrl;
    private String name;
    private String arrival_time;
    private String schedule_length;


    public ItemData(String status, String schedule_time, int imageUrl, String name, String arrival_time, String schedule_length){

        this.status = 			status;
        this.schedule_time = 	schedule_time;
        this.imageUrl = 		imageUrl;
        this.name = 			name;
        this.arrival_time = 	arrival_time;
        this.schedule_length = 	schedule_length;
    }

    public ItemData() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.status = schedule_time;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getSchedule_length() {
        return schedule_length;
    }

    public void setSchedule_length(String schedule_length) {
        this.schedule_length = schedule_length;
    }


}