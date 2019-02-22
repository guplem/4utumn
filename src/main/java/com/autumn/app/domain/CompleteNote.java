package com.autumn.app.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class CompleteNote {

    @NotNull(message = "The content cannot be null")
    @Size(min = 2, message = "The content must be longer")
    @Size(max = 500, message = "The content should be shorter")
    private String content;
    private long repliedNoteId = 0;
    private String username;
    private long id;
    private Date creationDate;
    private int voteStatus; //0- not voted, 1 voted positive, -1 voted negative
    private int positiveVotes = 0;
    private int negativeVotes = 0;
    String creatorDisplayName = "";
    String emoji = "";
    private int votes = 0;
    boolean viewed = false;

    public CompleteNote(long repliedNoteId , String content, String username, Date creation_date, long id, String voteStatus,String positiveVotes,String negativeVotes, String creatorDisplayName, boolean viewed, int votes){

        this.content = content;
        this.repliedNoteId = repliedNoteId;
        this.username = username;
        this.creationDate = creation_date;
        this.id = id;
        this.votes = votes;

        this.voteStatus = (voteStatus!=null)? ((Integer.parseInt(voteStatus)==0)? -1 : 1):0;
        this.negativeVotes = (negativeVotes!=null)? Integer.parseInt(negativeVotes) : 0;
        this.positiveVotes = (positiveVotes!=null)? Integer.parseInt(positiveVotes) : 0;
        this.creatorDisplayName = creatorDisplayName;
        this.viewed = viewed;

        this.emoji = calculateEmoji(this.votes);
    }

    public static String calculateEmoji(int votes){
        int oldKey = Integer.MIN_VALUE;
        String emoji = "\uD83D\uDE36"; //Default emoji (same as 0 votes)

        Map<Integer,String> map = new TreeMap<>();
        map.put(Integer.MIN_VALUE , "\uD83D\uDC7B");
        map.put(-1000 , "☠️");
        map.put(-643 , "\uD83D\uDCA9");
        map.put(-414 , "\uD83D\uDC7F");
        map.put(-266 , "\uD83D\uDE21");
        map.put(-171 , "\uD83D\uDE20");
        map.put(-110 , "\uD83E\uDD2E");
        map.put(-71 , "\uD83E\uDD22");
        map.put(-45 , "\uD83E\uDD25");
        map.put(-29 , "\uD83D\uDE27");
        map.put(-19 , "\uD83D\uDE1F");
        map.put(-12 , "\uD83D\uDE41");
        map.put(-7 , "\uD83D\uDE15");
        map.put(-3 , "\uD83D\uDE36");
        map.put(3 , "\uD83D\uDE42");
        map.put(7 , "\uD83D\uDE0A");
        map.put(12 , "\uD83E\uDD17");
        map.put(19 , "\uD83D\uDE01");
        map.put(29 , "\uD83E\uDD29");
        map.put(45 , "\uD83D\uDE0E");
        map.put(71 , "\uD83D\uDE0D");
        map.put(110 , "\uD83D\uDE3D");
        map.put(171 , "⭐️");
        map.put(266 , "\uD83D\uDD7A\uD83C\uDFFB");
        map.put(414 , "\uD83D\uDE80");
        map.put(643 , "\uD83D\uDC51");
        map.put(1000 , "⚜️");
        map.put(Integer.MAX_VALUE,"This should not be shown");


        for(int i : map.keySet()){
            if(oldKey <= votes && votes < i){
                emoji = map.get(oldKey);
                break;
            }
            oldKey = i;
        }


        return emoji;
    }

    public int getPositiveVotes() { return positiveVotes; }
    public int getNegativeVotes() { return negativeVotes; }
    public  int getVoteStatus(){return  this.voteStatus;}
    public String getCreatorDisplayName(){return this.creatorDisplayName;}
    public String getEmoji(){return this.emoji;}

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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setVoteStatus(int voteStatus) {
        this.voteStatus = voteStatus;
    }

    public void setPositiveVotes(int positiveVotes) {
        this.positiveVotes = positiveVotes;
    }

    public void setNegativeVotes(int negativeVotes) {
        this.negativeVotes = negativeVotes;
    }

    public void setCreatorDisplayName(String creatorDisplayName) {
        this.creatorDisplayName = creatorDisplayName;
    }
}
