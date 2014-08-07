package com.beemindz.miyotee.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TASK_DRAFT.
 */
public class TaskDraft {

    private Long id;
    private Integer taskId;
    private String userName;
    private String taskName;
    private String taskDescription;
    private java.util.Date reminderDate;
    private Boolean isReminder;
    private Boolean isComplete;
    private java.util.Date createdDate;
    private java.util.Date updatedDate;
    private Integer status;

    public TaskDraft() {
    }

    public TaskDraft(Long id) {
        this.id = id;
    }

    public TaskDraft(Long id, Integer taskId, String userName, String taskName, String taskDescription, java.util.Date reminderDate, Boolean isReminder, Boolean isComplete, java.util.Date createdDate, java.util.Date updatedDate, Integer status) {
        this.id = id;
        this.taskId = taskId;
        this.userName = userName;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.reminderDate = reminderDate;
        this.isReminder = isReminder;
        this.isComplete = isComplete;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public java.util.Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(java.util.Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Boolean getIsReminder() {
        return isReminder;
    }

    public void setIsReminder(Boolean isReminder) {
        this.isReminder = isReminder;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }

    public java.util.Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(java.util.Date createdDate) {
        this.createdDate = createdDate;
    }

    public java.util.Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(java.util.Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}