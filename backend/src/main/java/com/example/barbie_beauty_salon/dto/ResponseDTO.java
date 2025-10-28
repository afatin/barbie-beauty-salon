package com.example.barbie_beauty_salon.dto;

public class ResponseDTO {

    private String timestamp;
    private int status;
    private String title;
    private String detail;

    public ResponseDTO(String timestamp, int status, String title, String detail) {
        this.timestamp = timestamp;
        this.status = status;
        this.title = title;
        this.detail = detail;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
