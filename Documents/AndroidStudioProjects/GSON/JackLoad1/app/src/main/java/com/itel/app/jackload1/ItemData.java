package com.itel.app.jackload1;


public class ItemData implements java.io.Serializable {

    private String attendee_id;
    private String session_length;
    private String formatted_tm;
    private String date_rfc2822;
    private String name;
    private String photo_url;

    private String sched_tm;
    private String arrive_tm;
    private String attendee_name;
    private String status;

    public ItemData() {
    }


    public ItemData(String attendee_id, String session_length, String formatted_tm, String date_rfc2822, String name, String photo_url, String sched_tm, String arrive_tm, String attendee_name, String status) {
        this.attendee_id = attendee_id;
        this.session_length = session_length;
        this.formatted_tm = formatted_tm;
        this.date_rfc2822 = date_rfc2822;
        this.name = name;
        this.photo_url = photo_url;
        this.sched_tm = sched_tm;
        this.arrive_tm = arrive_tm;
        this.attendee_name = attendee_name;
        this.status = status;
    }

    public String getAttendee_id() {
        return attendee_id;
    }

    public void setAttendee_id(String attendee_id) {
        this.attendee_id = attendee_id;
    }

    public String getSession_length() {
        return session_length;
    }

    public void setSession_length(String session_length) {
        this.session_length = session_length;
    }

    public String getFormatted_tm() {
        return formatted_tm;
    }

    public void setFormatted_tm(String formatted_tm) {
        this.formatted_tm = formatted_tm;
    }

    public String getDate_rfc2822() {
        return date_rfc2822;
    }

    public void setDate_rfc2822(String date_rfc2822) {
        this.date_rfc2822 = date_rfc2822;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getSched_tm() {
        return sched_tm;
    }

    public void setSched_tm(String sched_tm) {
        this.sched_tm = sched_tm;
    }

    public String getArrive_tm() {
        return arrive_tm;
    }

    public void setArrive_tm(String arrive_tm) {
        this.arrive_tm = arrive_tm;
    }

    public String getAttendee_name() {
        return attendee_name;
    }

    public void setAttendee_name(String attendee_name) {
        this.attendee_name = attendee_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "attendee_id='" + attendee_id + '\'' +
                ", session_length='" + session_length + '\'' +
                ", formatted_tm='" + formatted_tm + '\'' +
                ", date_rfc2822='" + date_rfc2822 + '\'' +
                ", name='" + name + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", sched_tm='" + sched_tm + '\'' +
                ", arrive_tm='" + arrive_tm + '\'' +
                ", attendee_name='" + attendee_name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
