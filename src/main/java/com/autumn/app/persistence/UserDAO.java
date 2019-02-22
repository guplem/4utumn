package com.autumn.app.persistence;

import com.autumn.app.domain.Vote;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.autumn.app.domain.User;

@Repository
public class UserDAO {

    //region SQL_INSTRUCTIONS
    private final String REGISTER_USER = "INSERT INTO Users (username, email, password, display_name, reg_date) VALUES (?,?,?,?,(SELECT SYSDATE() FROM DUAL))";
    private final String FOLLOW_USER = "INSERT INTO Follows (my_username, followed_username) VALUES (?,?) ON DUPLICATE KEY UPDATE my_username = 'r', followed_username = 'r'";
    private final String FOLLOW_USER_SECOND = "DELETE FROM Follows WHERE my_username = 'r' AND  followed_username = 'r';";
    private final String BLOCK_USER = "INSERT INTO Blocks (my_username, blocked_username) VALUES (?,?) ON DUPLICATE KEY UPDATE my_username = 'r', blocked_username = 'r'";
    private final String BLOCK_USER_SECOND = "DELETE FROM Blocks WHERE my_username = 'r' AND  blocked_username = 'r';";
    private final String INSERT_VOTE= "INSERT INTO Votes(is_positive,note_id,username) VALUES (?,?,?)";//" ON DUPLICATE KEY UPDATE is_positive=? ";
    private final String UPDATE_VOTE= "UPDATE Votes SET is_positive = ? WHERE note_id = ? AND username = ?";//" ON DUPLICATE KEY UPDATE is_positive=? ";
    private final String MODIFY_KARMA= "UPDATE Users SET votes = votes + ? WHERE username = (SELECT username FROM Notes WHERE note_id = ?)";
    private final String DELETE_USER = "DELETE FROM Users WHERE username = ?"; //Hemos optado a eliminar por completo el usuario ya que por la nueva ley de protección de datos se debe justifar el uso de la información recabada del usuario y al no ser utilizada debe destruirse, a parte legalmente to-do usuario tiene derecho a la eliminación permanente de sus datos en internet.

    private final String CHANGE_USER_INFO = "UPDATE Users SET email = ?,password = ?,display_name =? WHERE username = ?";


    //SELECTS
    private final String GET_USER_INFO = "SELECT username, email, display_name, reg_date, enabled, role FROM Users WHERE username = ?";
    private final String GET_USER_BASIC_INFO = "SELECT username, display_name, enabled FROM Users WHERE username = ?";
    private final String GET_PASSWORD = "SELECT password FROM Users WHERE username = ?";
    private final String GET_BLOCKED_USERS = "SELECT username, display_name FROM Users WHERE username IN (SELECT blocked_username FROM Blocks WHERE my_username = ?) AND enabled = true";
    private final String GET_FOLLOWED_USERS = "SELECT username, display_name FROM Users WHERE username IN (SELECT followed_username FROM Follows WHERE my_username = ?) AND enabled = true";
    private final String GET_FOLLOWERS = "SELECT username, display_name FROM Users WHERE username IN (SELECT my_username FROM Follows WHERE followed_username = ?) AND enabled = true";
    private final String GET_RANDOM_USERS = "SELECT username, display_name FROM Users WHERE username NOT IN (SELECT blocked_username FROM Blocks WHERE my_username = ?) AND username NOT IN (SELECT followed_username FROM Follows WHERE my_username = ?) AND (username != ?) AND enabled = true ORDER BY RAND() LIMIT ?";
    private final String GET_SUGGESTED_USERS = "SELECT username, display_name FROM Users WHERE username NOT IN (SELECT blocked_username FROM Blocks WHERE my_username = ?) AND username NOT IN (SELECT followed_username FROM Follows WHERE my_username = ?) AND (username != ?) AND enabled = true ORDER BY votes DESC LIMIT ?";
    private final String GET_ALL_USERS = "SELECT username, email, display_name, reg_date, enabled FROM Users";
    private final String GET_USER_VOTES= "SELECT * FROM Votes WHERE username =  ?";
    private final String GET_USERS_BY_SEARCH = "SELECT username, display_name FROM Users WHERE (LOWER(username) LIKE ? OR LOWER(display_name) LIKE ?) AND username NOT IN (SELECT blocked_username FROM Blocks WHERE my_username = ?) ORDER BY votes DESC LIMIT 10";
    private final String IS_FOLLOWING = "SELECT COUNT(*) AS counter FROM Follows WHERE my_username = ? AND followed_username = ?";
    private final String IS_BLOCKED = "SELECT COUNT(*) AS counter FROM Blocks WHERE my_username = ? AND blocked_username = ?";

    private final String GET_ALL = "SELECT(SELECT COUNT(my_username) FROM Follows WHERE followed_username = ?) AS followers, (SELECT COUNT(followed_username) FROM Follows WHERE my_username = ?) AS follows, (SELECT COUNT(note_id) FROM Notes WHERE username = ? AND replied_note_id IS NULL) AS notes, (SELECT votes AS counter FROM Users WHERE username = ?) AS votes";
    //DELETES
    private final String UNFOLLOW_USER = "DELETE FROM Follows WHERE my_username = ? AND followed_username = ?";
    private final String UNBLOCK_USER = "DELETE FROM Blocks WHERE my_username = ? AND blocked_username = ?";
    //endregion

    private JdbcTemplate jdbcTemplate;

    //Constructor
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //MAPPER
    private final RowMapper<User> mapper = (resultSet, i) -> {
        return new User.UserBuilder()
                .username((doesColumnExist(resultSet.getMetaData(),"username"))? resultSet.getString("username") : "")
                .email((doesColumnExist(resultSet.getMetaData(),"email"))? resultSet.getString("email") : "")
                .password((doesColumnExist(resultSet.getMetaData(),"password"))? resultSet.getString("password") : "")
                .displayName((doesColumnExist(resultSet.getMetaData(),"display_name"))? resultSet.getString("display_name") : "")
                .enabled((doesColumnExist(resultSet.getMetaData(),"enabled"))? resultSet.getBoolean("enabled") : true)
                .rol((doesColumnExist(resultSet.getMetaData(),"role"))? resultSet.getString("role") : "")
                .build();

    };

    private final RowMapper<User> mapperOthers = (resultSet, i) -> {
        return new User.UserBuilder()
                .username((doesColumnExist(resultSet.getMetaData(),"username"))? resultSet.getString("username") : "")
                .displayName((doesColumnExist(resultSet.getMetaData(),"display_name"))? resultSet.getString("display_name") : "")
                .enabled((doesColumnExist(resultSet.getMetaData(),"enabled"))? resultSet.getBoolean("enabled") : true)
                .build();

    };

    private final RowMapper<Integer> counterMapper = (resultSet, i) -> {
        return resultSet.getInt("counter");
    };

    private final RowMapper<Map<String,Integer>> socialUserInfoMapper = (resultSet, i) -> {
      Map<String,Integer> map = new HashMap<>();
      map.put("followers",resultSet.getInt("followers"));
      map.put("follows",resultSet.getInt("follows"));
      map.put("notes",resultSet.getInt("notes"));
      map.put("votes",resultSet.getInt("votes")*42);
      return map;
    };

    private final RowMapper<Vote> voteRowMapper= (resultSet, i) -> {
        return new Vote.VoteBuilder()
                .isPositive((doesColumnExist(resultSet.getMetaData(),"is_positive"))? resultSet.getBoolean("is_positive"):null)
                .noteID((doesColumnExist(resultSet.getMetaData(),"note_id"))? resultSet.getLong("note_id") : null)
                .username((doesColumnExist(resultSet.getMetaData(),"username"))? resultSet.getString("username"): "")
                .build();
    };

    //region INSERTS


    public int registerUser(User user){
        return jdbcTemplate.update(REGISTER_USER, user.getUsername(), user.getEmail(), user.getPassword(), user.getDisplay_name());
    }

    public int followUser(String my_username, String followed_username){
        int i = jdbcTemplate.update(FOLLOW_USER, my_username, followed_username);
        jdbcTemplate.update(FOLLOW_USER_SECOND);
        return i;
    }

    public int blockUser(String my_username, String blocked_username){
        int i = jdbcTemplate.update(BLOCK_USER, my_username, blocked_username);
        jdbcTemplate.update(BLOCK_USER_SECOND);
        return i;
    }
    //endregion

    //region SELECTS
    public Map<String,Integer> getAllSocialInfo(String username){
        return jdbcTemplate.queryForObject(GET_ALL, socialUserInfoMapper, username, username, username, username);
    }

    public boolean isBlocked(String my_username, String blocked_username){
        return jdbcTemplate.queryForObject(IS_BLOCKED, counterMapper,my_username,blocked_username) == 1;
    }

    public boolean isFollowing(String my_username, String followed_username){
        return jdbcTemplate.queryForObject(IS_FOLLOWING, counterMapper,my_username,followed_username) == 1;
    }

    public User getUserInfo(String username){
        return jdbcTemplate.queryForObject(GET_USER_INFO, mapper, username);
    }

    public User getUserBasicInfo(String username){
        return jdbcTemplate.queryForObject(GET_USER_BASIC_INFO, mapper, username);
    }

    public String getPassword(String username){
        return jdbcTemplate.query(GET_PASSWORD, mapper, username).get(0).getPassword();
    }

    public List<User> getBlockedUsers(String username){
        return jdbcTemplate.query(GET_BLOCKED_USERS, mapperOthers, username);
    }

    public List<User> getFollowedUsers(String username){
        return jdbcTemplate.query(GET_FOLLOWED_USERS, mapperOthers, username);
    }

    public List<User> getFollowers(String username){
        return jdbcTemplate.query(GET_FOLLOWERS, mapperOthers, username);
    }

    public List<User> getAllUsers(){
        return jdbcTemplate.query(GET_ALL_USERS, mapper);
    }

    public List<User> getUsersBySearch(String searchText, String username){
        return jdbcTemplate.query(GET_USERS_BY_SEARCH,mapper,"%"+searchText+"%", "%"+searchText+"%", username);
    }

    public List<User> getRandomUsers(String username, int numberOfUsers){
        return jdbcTemplate.query(GET_RANDOM_USERS, mapper, username, username, username, numberOfUsers);
    }

    public List<User> getSuggestedUsers(String username, int numberOfUsers){
        return jdbcTemplate.query(GET_SUGGESTED_USERS, mapper, username, username, username, numberOfUsers);
    }

    //endregion

    //region UPDATES
    public int changeUserInfo(String username,String email, String password,String display_name ){

        return jdbcTemplate.update(CHANGE_USER_INFO, email,password,display_name, username);
    }



    public int deleteUser(String username){
        return jdbcTemplate.update(DELETE_USER, username);
    }
    //endregion

    //region DELETES
    public int unfollowUser(String my_username, String followed_username){
        return jdbcTemplate.update(UNFOLLOW_USER, my_username, followed_username);
    }

    public int unblockUser(String my_username, String blocked_username){
        return jdbcTemplate.update(UNBLOCK_USER, my_username, blocked_username);
    }

    //endregion

    //region Votes
    //region INSERTS
    public int insertVote(Vote vote){

        try {
            jdbcTemplate.update(INSERT_VOTE, vote.isPositive(), vote.getNoteID(), vote.getUsername());
            return jdbcTemplate.update(MODIFY_KARMA, (vote.isPositive())? 1:-1,vote.getNoteID());
        }catch(DuplicateKeyException er){
            jdbcTemplate.update(UPDATE_VOTE, vote.isPositive(), vote.getNoteID(), vote.getUsername());
            return jdbcTemplate.update(MODIFY_KARMA, (vote.isPositive())? 2:-2,vote.getNoteID());
        }
    }
    //endregion

    //region SELECTS
    public List<Vote> getUserVotes(String username){return jdbcTemplate.query(GET_USER_VOTES,voteRowMapper,username);}
    //endregion

    // endregion



    public static boolean doesColumnExist(ResultSetMetaData meta, String columnName) throws SQLException{
        int numCol = meta.getColumnCount();
        for (int i = 1; i <= numCol; i++) {
            if(meta.getColumnName(i).equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

}
