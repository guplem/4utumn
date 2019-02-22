package com.autumn.app.domain;

public class Vote {
    private boolean isPositive ;
    private long noteID; //The ID of the NOTE voted
    private String username; //The username of the user who voted

    public Vote(){}
    public Vote(boolean isPositive, long noteID, String username){
        this.isPositive=isPositive;
        this.noteID=noteID;
        this.username=username;
    }

    public Vote(long noteID, String username){ //for temporal votes
        this.noteID=noteID;
        this.username=username;
    }
    public static class VoteBuilder{
        String username = "";
        long noteID;
        boolean isPositive=false;

        public VoteBuilder username(String username) {
            this.username = username;
            return this;
        }

        public VoteBuilder noteID(long noteID) {
            this.noteID = noteID;
            return this;
        }

        public VoteBuilder isPositive(Boolean isPositive) {
            this.isPositive = isPositive;
            return this;
        }




        public Vote build(){
            return new Vote(isPositive, noteID,username);
        }
    }

    public void setIsPositive(boolean status) {
        isPositive = status;
    }

    public void setNoteID(long noteID) {
        this.noteID = noteID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public long getNoteID() {
        return noteID;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Vote)obj).getNoteID() == this.getNoteID() && ((Vote) obj).getUsername().equals(this.getUsername());
    }
}
