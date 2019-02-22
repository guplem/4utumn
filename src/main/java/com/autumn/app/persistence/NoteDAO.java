package com.autumn.app.persistence;

import com.autumn.app.domain.CompleteNote;
import com.autumn.app.domain.Note;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NoteDAO {

    private JdbcTemplate jdbcTemplate;

    //SELECTS
    private final String FIND_ALL_NOTES_BY_USER = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE n.username = ? ORDER BY n.note_id DESC";
    private final String GET_NOTE_BY_ID = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE n.note_id = ? ORDER BY n.note_id DESC";
    private final String GET_ALL_REPLY_NOTES = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE replied_note_id = ? ORDER BY n.note_id DESC";
    private final String GET_BY_CONTENT = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE content LIKE ? AND replied_note_id IS NULL AND username NOT IN (SELECT blocked_username FROM Blocks WHERE my_username = ?) ORDER BY note_id DESC LIMIT 25;";
    private final String FIND_NOTES_FROM_FOLLOWEDS = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE (username IN (SELECT followed_username FROM Follows WHERE my_username = ?) OR username = ?) AND replied_note_id IS NULL ORDER BY note_id DESC;";
    private final String GET_REPLIES_OF_USER = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE n.replied_note_id IN (SELECT m.note_id FROM Notes m WHERE m.username = ?) AND n.username != ? AND n.viewed = 0 ORDER BY note_id DESC";
    private final String GET_MENTIONS_OF_USER = "SELECT n.replied_note_id, n.content, n.username, n.creation_date, n.note_id, (SELECT is_positive FROM Votes WHERE note_id = n.note_id AND username = ?) AS voteStatus, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = n.note_id AND is_positive = FALSE) AS negatives, (SELECT display_name FROM Users WHERE username=n.username) AS display_name, n.viewed, (SELECT votes AS counter FROM Users WHERE username = n.username) AS votes FROM Notes n WHERE n.content LIKE ? ORDER BY n.note_id DESC LIMIT 100";


    private final String GET_10_LAST_NOTES_BY_USER = "SELECT * FROM Notes WHERE username = ? ORDER BY note_id DESC LIMIT 10;";
    private final String GET_NUM_OF_POSITIVE_OR_NEGATIVE_VOTES_BY_NOTE= " SELECT COUNT(is_positive) FROM Votes WHERE (note_id = ? AND is_positive = ?)";

    private final String GET_COMPLETE_NOTES_INFO = "SELECT (SELECT COUNT(is_positive) FROM Votes WHERE note_id = ? AND is_positive = TRUE) AS positives, (SELECT COUNT(is_positive) FROM Votes WHERE note_id = ? AND is_positive = FALSE) AS negatives, (SELECT is_positive FROM Votes WHERE note_id = ? AND username = ?) AS voteStatus, u.display_name FROM Users u WHERE u.username = ?";

    private final String GET_USER_NOTES_COUNT = "SELECT COUNT(note_id) AS notes FROM Notes WHERE username = ? AND replied_note_id IS NULL";


    //INSERTS
    private final String INSERT_NOTE = "INSERT INTO Notes (content, creation_date, username) VALUES (?,(SELECT SYSDATE() FROM DUAL),?);";
    private final String INSERT_REPLY_NOTE = "INSERT INTO Notes (content, creation_date, username, replied_note_id) VALUES (?,(SELECT SYSDATE() FROM DUAL),?,?);";

    //DELETES
    private final String DELETE_NOTE = "DELETE FROM Notes WHERE note_id = ?;";

    //UPDATES
    private final String UPDATE_VIEWED = "UPDATE Notes SET viewed = 1 WHERE note_id = ?";


    private final RowMapper<Note> mapper = (resultSet, i) -> {
        return new Note(
                resultSet.getLong("replied_note_id"),
                resultSet.getString("content"),
                resultSet.getString("username"),
                resultSet.getDate("creation_date"),
                resultSet.getLong("note_id")
        );
    };

    private final RowMapper<CompleteNote> mapperComplete = (resultSet, i) -> {
        return new CompleteNote(resultSet.getLong("replied_note_id"),
                resultSet.getString("content"),
                resultSet.getString("username"),
                resultSet.getDate("creation_date"),
                resultSet.getLong("note_id"),
                resultSet.getString("voteStatus"),
                resultSet.getString("positives"),
                resultSet.getString("negatives"),
                resultSet.getString("display_name"),
                resultSet.getBoolean("viewed"),
                resultSet.getInt("votes"));
    };

    private final RowMapper<Map<String,String>> noteInfoMapper = (resultSet, i) -> {
        Map<String,String> map = new HashMap<>();
        map.put("positives",resultSet.getString("positives"));
        map.put("negatives",resultSet.getString("negatives"));
        map.put("voteStatus",resultSet.getString("voteStatus"));
        map.put("display_name",resultSet.getString("display_name"));

        return map;
    };

    private final RowMapper<Integer> countMapper = (resultSet, i) -> {
        return resultSet.getInt("notes");
    };


    public NoteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //SELECTS

        public List<CompleteNote> getNotesByUser(String myUsername, String noteUsername){
            return jdbcTemplate.query(FIND_ALL_NOTES_BY_USER, mapperComplete, myUsername, noteUsername);
        }

        public CompleteNote getNoteById(String myUsername, long id){
            return jdbcTemplate.queryForObject(GET_NOTE_BY_ID, mapperComplete, myUsername, id);
        }

        public List<CompleteNote> getReplyNotesFromNote(String myUsername, long id){
            return jdbcTemplate.query(GET_ALL_REPLY_NOTES, mapperComplete, myUsername, id);
        }

        public List<CompleteNote> getNotesByContent(String myUsername, String content, String username){
            return jdbcTemplate.query(GET_BY_CONTENT, mapperComplete, myUsername,"%"+content+"%", username);
        }

        public List<CompleteNote> getNotifications(String myUsername){
            return jdbcTemplate.query(GET_REPLIES_OF_USER, mapperComplete, myUsername, myUsername, myUsername);
        }

        public List<CompleteNote> getMentions(String myUsername){
            return jdbcTemplate.query(GET_MENTIONS_OF_USER, mapperComplete, myUsername, "%@"+myUsername+" %");
        }



    public Map<String,String> getNoteInfo(long noteId, String my_username, String note_username){
            return jdbcTemplate.queryForObject(GET_COMPLETE_NOTES_INFO, noteInfoMapper, noteId,noteId,noteId,my_username,note_username);
        }

        public int getNumOfVotes(long noteID,boolean posOrNeg){

            return jdbcTemplate.queryForObject(GET_NUM_OF_POSITIVE_OR_NEGATIVE_VOTES_BY_NOTE,Integer.class,noteID,posOrNeg);
        }



        public List<CompleteNote> getNotesFromFolloweds(String username){
            return jdbcTemplate.query(FIND_NOTES_FROM_FOLLOWEDS, mapperComplete, username, username,username);
        }

        public List<Note> getTenLastNotesByUser(String username){
            return jdbcTemplate.query(GET_10_LAST_NOTES_BY_USER, mapper, username);
        }





        public int getUserNotesCount(String username){
            return jdbcTemplate.queryForObject(GET_USER_NOTES_COUNT, countMapper, username);
        }

    //INSERTS

    public int createNote(Note note){
        if(note.getRepliedNoteId() == 0)
            return jdbcTemplate.update(INSERT_NOTE, note.getContent()+" ", note.getUsername());
        return jdbcTemplate.update(INSERT_REPLY_NOTE, note.getContent()+" ", note.getUsername(), note.getRepliedNoteId());
    }

    //DELETES

    public int deleteNote(long id){
        return jdbcTemplate.update(DELETE_NOTE, id);
    }

    //UPDATES

    public int updateViewed(long id){

        return jdbcTemplate.update(UPDATE_VIEWED, id);
    }

}
