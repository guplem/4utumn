package com.autumn.app.management;

import com.autumn.app.domain.CompleteNote;
import com.autumn.app.domain.Note;
import com.autumn.app.domain.User;
import com.autumn.app.domain.Vote;
import com.autumn.app.persistence.NoteDAO;
import com.autumn.app.persistence.UserDAO;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("dbManager")
public class DBManager {

    private NoteDAO noteDAO;
    private UserDAO userDAO;

    public DBManager(NoteDAO noteDAO, UserDAO userDAO) {
        this.noteDAO = noteDAO;
        this.userDAO = userDAO;
    }

    //region USER

        //region INSERTS
        public int registerUser(User user){
            return userDAO.registerUser(user);
        }

        public int followUser(String my_username, String followed_username){

            return userDAO.followUser(my_username,followed_username);
        }

        public int blockUser(String my_username, String blocked_username){
            return userDAO.blockUser(my_username, blocked_username);
        }
        //endregion

        //region SELECTS

            public Map<String,Integer> getAllSocialInfo(String username){
                return userDAO.getAllSocialInfo(username);
            }

            public boolean isFollowing(String my_username, String followed_username){
                return userDAO.isFollowing(my_username,followed_username);
            }
            public boolean isBlocked(String my_username, String blocked_username){
                return userDAO.isBlocked(my_username,blocked_username);
            }

            public User getUserInfo(String username){
                User user = userDAO.getUserInfo(username);
                user.setVoteArrList(this.getVotesByUser(user.getUsername()));
                return user;
            }

            public User getUserBasicInfo(String username){

                User user = userDAO.getUserBasicInfo(username);
                return user;
            }

            public String getPassword(String username){
                return userDAO.getPassword(username);
            }

            public List<User> getBlockedUsers(String username){
                return userDAO.getBlockedUsers(username);
            }

            public List<User> getFollowedUsers(String username){
                return userDAO.getFollowedUsers(username);
            }

            public List<User> getFollowers(String username){
            return userDAO.getFollowers(username);
        }

            public List<User> getAllUsers(){ return userDAO.getAllUsers(); }

            public List<User> getUsersBySearch(String searchText, String username){
                return userDAO.getUsersBySearch(searchText, username);
            }

            public List<User> getRandomUsers(String username, int numberOfUsers){
                    return userDAO.getRandomUsers(username, numberOfUsers);
            }

            public List<User> getSuggestedUsers(String username, int numberOfUsers){
                return userDAO.getSuggestedUsers(username, numberOfUsers);
            }

        //endregion

        //region UPDATES

    public int changeUserInfo(String username,String email,String password,String display_name){
        return userDAO.changeUserInfo(username,email,password,display_name);
    }

            public int deleteUser(String username){
                return userDAO.deleteUser(username);
            }
        //endregion

        //region DELETES
            public int unfollowUser(String my_username, String followed_username){
                return userDAO.unfollowUser(my_username,followed_username);
            }

            public int unblockUser(String my_username, String blocked_username){
                return userDAO.unblockUser(my_username,blocked_username);
            }
        //endregion

    //endregion

    //region NOTE

        //region SELECTS

            public List<CompleteNote> getNotesByUser(Principal principal, String noteUsername){
                return noteDAO.getNotesByUser((principal==null)? "":principal.getName(), noteUsername);
            }

            public CompleteNote getNoteById(Principal principal, long id){
                return noteDAO.getNoteById((principal==null)? "":principal.getName(), id);
            }

            public List<CompleteNote> getReplyNotesFromNote(Principal principal, long id){
                return noteDAO.getReplyNotesFromNote((principal==null)? "":principal.getName(), id);
            }

            public List<CompleteNote> getNotesByContent(Principal principal, String content, String username){
                return noteDAO.getNotesByContent((principal==null)? "":principal.getName(), content, username);
            }

            public List<CompleteNote> getNotifications(Principal principal){
                return noteDAO.getNotifications((principal==null)? "":principal.getName());
            }

            public List<CompleteNote> getMentions(Principal principal){
                return noteDAO.getMentions((principal==null)? "":principal.getName());
            }


            public Map<String,String> getNoteInfo(long noteId, String my_username, String note_username){
                return noteDAO.getNoteInfo(noteId,my_username,note_username);
            }

            public int getNumOfVotes(long noteID,boolean posOrNeg){ return  noteDAO.getNumOfVotes(noteID,posOrNeg);}

            public List<CompleteNote> getNotesFromFolloweds(String username){
                return noteDAO.getNotesFromFolloweds(username);
            }



            public List<Note> getTenLastNotesByUser(String username){
                return noteDAO.getTenLastNotesByUser(username);
            }







            public int getUserNotesCount(String username){
                return noteDAO.getUserNotesCount(username);
            }

        //endregion

        //region INSERTS
            public int createNote(Note note){
            return noteDAO.createNote(note);
        }
        //endregion

        //region DELETES
            public int deleteNote(long id){
            return noteDAO.deleteNote(id);
        }

        //endregion

        //region UPDATES
            public int updateViewed(String id){
                return noteDAO.updateViewed(Long.parseLong(id));
            }
        //endregion

    //endregion

    //region VOTES

        //region SELECTS
            public ArrayList<Vote> getVotesByUser (String username){ return(ArrayList<Vote>) userDAO.getUserVotes(username);}
        //endregion

        //region INSERTS
            public int insertVote( Vote vote){ return userDAO.insertVote(vote); }
        //endregion

    //endregion

}
