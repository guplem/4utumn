package com.autumn.app.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

public class Note {

    @NotNull(message = "The content cannot be null")
    @Size(min = 2, message = "The content must be longer")
    @Size(max = 500, message = "The content should be shorter")
    private String content;
    private long repliedNoteId = 0;
    private String username;
    private long id;
    private Date creationDate;

    public Note(){}

    public Note(long repliedNoteId ,String content, String username, Date creation_date, long id)
    {
        this.content = content;
        this.repliedNoteId = repliedNoteId;
        this.username = username;
        this.creationDate = creation_date;
        this.id = id;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getRepliedNoteId() {
        return repliedNoteId;
    }

    public void setRepliedNoteId(long repliedNoteId) {
        this.repliedNoteId = repliedNoteId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creation_date) {
        this.creationDate = creation_date;
    }

    //Further appliances
    private int positiveVotes=0;
    private int negativeVotes=0;

    @Override
    public String toString() {
        return "Note{" +
                "content='" + content + '\'' +
                ", repliedNoteId=" + repliedNoteId +
                ", username='" + username + '\'' +
                ", id=" + id +
                ", creation_date=" + creationDate +
                ", positiveVotes=" + positiveVotes +
                ", negativeVotes=" + negativeVotes +
                '}';
    }
}


