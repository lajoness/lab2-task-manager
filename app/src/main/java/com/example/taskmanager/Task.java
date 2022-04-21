package com.example.taskmanager;

public class Task {

    private String title;
    private String description;
    private String type;
    private String deadline;
    private boolean status;

    public Task(String title, String description, String type, String deadline) {

        this.title = title;
        this.description = description;
        this.type = type;
        this.deadline = deadline;
        this.status = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
