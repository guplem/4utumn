package com.autumn.app.webControllers;

import com.autumn.app.domain.*;
import com.autumn.app.management.DBManager;
//import org.omg.CORBA.PRIVATE_MEMBER;
//import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

//import javax.jws.WebParam;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

import static com.autumn.app.domain.CompleteNote.calculateEmoji;

@Controller
public class WebController {

    private DBManager dbmanager;
    private User currUser;

    public WebController(DBManager dbmanager) {
        this.dbmanager = dbmanager;
    }


    @GetMapping("note/{noteId}")
    public String note(@PathVariable String noteId, Model model, Principal principal){

        model.addAttribute("principal", principal);

        CompleteNote note = dbmanager.getNoteById(principal, Long.parseLong(noteId));

        model.addAttribute("principalNote", note);

        model.addAttribute("notes", dbmanager.getReplyNotesFromNote(principal, note.getId()));

        return "note";
    }

    @PostMapping("note/{noteId}/positive")
    public String votePositive(@PathVariable String noteId, Model model, Principal principal){
        Vote vote = new Vote();
        vote.setIsPositive(true);
        vote.setNoteID(Long.parseLong(noteId));
        vote.setUsername(principal.getName());
        dbmanager.insertVote(vote);
        long parent = dbmanager.getNoteById(principal, Long.parseLong(noteId)).getRepliedNoteId();
        /*if(parent == 0)
            return "redirect:";//note/" + noteId;
        else
            return "redirect:/note/" + parent;*/
        return "redirect:/goBack";
    }
    @PostMapping("note/{noteId}/negative")
    public String voteNegative(@PathVariable String noteId, Model model, Principal principal){
        Vote vote = new Vote();
        vote.setIsPositive(false);
        vote.setNoteID(Long.parseLong(noteId));
        vote.setUsername(principal.getName());
        dbmanager.insertVote(vote);
        long parent = dbmanager.getNoteById(principal, Long.parseLong(noteId)).getRepliedNoteId();
        /*if(parent == 0)
            return "redirect:";//note/" + noteId;
        else
            return "redirect:/note/" + parent;*/
        return "redirect:/goBack";
    }

    @GetMapping("createNote")
    public String createNote(Model model, Principal principal){
        model.addAttribute( "note", new Note());
        model.addAttribute("principal", principal);
        model.addAttribute("errors", new ArrayList<>());
        return "createNote";
    }


    @PostMapping("createNote")
    public String createNote(@Valid Note note, Errors errors, Principal principal, Model model){

        List<String> errorsStrings = new ArrayList<>();
        errors.getAllErrors().forEach(e->errorsStrings.add(e.getDefaultMessage()));
        model.addAttribute("errors", errorsStrings);


        if(errors.hasErrors())
            return "createNote";

        note.setUsername(principal.getName());
        dbmanager.createNote(note);

        return "redirect:/feed";
    }

    @GetMapping("createNote/{parentNote}")
    public String createNote(@PathVariable String parentNote, Model model, Principal principal){
        model.addAttribute("notes", dbmanager.getNoteById(principal, Long.parseLong(parentNote)));
        model.addAttribute( "note", new Note());
        model.addAttribute("principal", principal);
        model.addAttribute("errors", new ArrayList<>());
        return "createNote";
    }

    @PostMapping("createNote/{parentNote}")
    public String createReplyNote(@PathVariable String parentNote, @Valid Note note, Errors errors, Principal principal, Model model) {

        model.addAttribute("errors", new ArrayList<>());

        if(errors.hasErrors())
            return "redirect:/createNote/"+parentNote;

        note.setUsername(principal.getName());
        note.setRepliedNoteId(Long.parseLong(parentNote));
        dbmanager.createNote(note);

        return "redirect:/note/"+note.getRepliedNoteId();
    }

    @GetMapping("search/{searchText}")
    public String search(@PathVariable String searchText, Model model, Principal principal){

        switch (searchText){

            //Quick access search (DO NOT ADD BREAK; at the end)
            case "suggested": if(principal != null) return "redirect:/users/"+principal.getName()+"/suggested";
            case "random": if(principal != null) return "redirect:/users/"+principal.getName()+"/random";
            case "followers": if(principal != null) return "redirect:/users/"+principal.getName()+"/followers";
            case "followed": if(principal != null) return "redirect:/users/"+principal.getName()+"/followed";
            case "profile": if(principal != null) return "redirect:/profile/"+principal.getName();
            case "feed": return "redirect:/feed";

            default:
            model.addAttribute("principal", principal);
            if(principal != null)
                model.addAttribute ( "users", parseToCompleteUser ( principal,  dbmanager.getUsersBySearch(searchText, principal.getName()) ) ) ;
            else
                model.addAttribute ( "users", parseToCompleteUser ( principal,  dbmanager.getUsersBySearch(searchText, "") ) ) ;

            if (principal != null)
                model.addAttribute( "notes", dbmanager.getNotesByContent(principal, searchText, principal.getName()));
            else
                model.addAttribute( "notes", dbmanager.getNotesByContent(principal, searchText, ""));

            model.addAttribute("searchText", searchText);

        }

        return "search";
    }

    @GetMapping("users/{username}/{type}")
    public String userList(@PathVariable String type, @PathVariable String username, Model model, Principal principal){

        switch (type)
        {
            case "blocked":
                if (principal != null)
                    if (username.compareTo(principal.getName()) != 0) return "redirect:/users/"+principal.getName()+"/blocked";
                model.addAttribute( "users", parseToCompleteUser(principal, dbmanager.getBlockedUsers(username)));
                model.addAttribute( "typelist", "blocked users");
                break;
            case "followers":
                model.addAttribute( "users", parseToCompleteUser(principal, dbmanager.getFollowers(username)));
                model.addAttribute( "typelist", "followers");
                break;
            case "followed":
                model.addAttribute( "users", parseToCompleteUser(principal, dbmanager.getFollowedUsers(username)));
                model.addAttribute( "typelist", "followed users");
                break;
            case "random":
                model.addAttribute( "users", parseToCompleteUser(principal, dbmanager.getRandomUsers(username, 10)));
                model.addAttribute( "typelist", "random users");
                break;
            case "suggested":
                model.addAttribute( "users", parseToCompleteUser(principal, dbmanager.getSuggestedUsers(username, 20)));
                model.addAttribute( "typelist", "suggested users");
                break;
            default: return "redirect:/users/"+username+"/random";
        }


        model.addAttribute("principal", principal);
        model.addAttribute("username", username);
        return "users";
    }




    @GetMapping("registerUser")
    public String registerUser(Model model, Principal principal){
        model.addAttribute( "user", new User());
        model.addAttribute("principal", principal);
        model.addAttribute("errors", new ArrayList<>());
        return "registerUser";
    }


    @PostMapping("registerUser")
    public String registerUser(@Valid User user, Errors errors, Model model){

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        try {
            dbmanager.registerUser(user);
        }catch (DuplicateKeyException e){
            errors.rejectValue("username", "error.user", "Username already exists");
        }

        List<String> errorsStrings = new ArrayList<>();
        errors.getAllErrors().forEach(e->errorsStrings.add(e.getDefaultMessage()));
        model.addAttribute("errors", errorsStrings);

        if(errors.hasErrors())
            return "registerUser";

        currUser = user;

        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        GrantedAuthority rol = new SimpleGrantedAuthority("ROLE_" + user.getRol());
        grantedAuthorities.add(rol);
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(user.getUsername(), encodedPassword, grantedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);


        return "redirect:/profile/tutorial";
    }

    @GetMapping ("/configuration")
    public String changeUserInfo (Model model, Principal principal){
        currUser=dbmanager.getUserInfo(principal.getName());
        model.addAttribute("toUpdate", new UserToUpdate(currUser));
        model.addAttribute("principal",principal);
        model.addAttribute("errors", new ArrayList<>());

        return "configuration";
    }

    @PostMapping("/configuration")
    public String changeUserInfo(@Valid UserToUpdate toUpdate, Errors errors, Model model,Principal principal){
        toUpdate.setUsername(currUser.getUsername());
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        List<String> errorsStrings = new ArrayList<>();




        //Check current password
        if (toUpdate.getPassword().length() <= 0){
            errorsStrings.add("Password is required to update the configuration");
        } else {
            if(!encoder.matches(toUpdate.getPassword(),dbmanager.getPassword(toUpdate.getUsername())))
                errorsStrings.add("The current password introduced is not correct");
        }


        //Check new passwords
        if (toUpdate.getNewPassword().length()>2) {
            if (toUpdate.getNewPassword().compareTo(toUpdate.getNewPasswordConfirmed()) != 0)
                errorsStrings.add("New passwords don't match");
        } else {
            if(toUpdate.getNewPassword().length()<=2&&toUpdate.getNewPassword().length()>0 || toUpdate.getNewPasswordConfirmed().length()<=2&&toUpdate.getNewPasswordConfirmed().length()>0)
                errorsStrings.add("New password must be longer");//We could check only one, because they are equal
            else {
                //New Password is correct
                toUpdate.setNewPassword(toUpdate.getPassword());
                toUpdate.setNewPasswordConfirmed(toUpdate.getPassword());
            }
        }

        errors.getAllErrors().forEach(e->errorsStrings.add(e.getDefaultMessage()));



        if(errorsStrings.size()>0) {
            if(currUser==null) currUser=dbmanager.getUserInfo(principal.getName());
            model.addAttribute("toUpdate", new UserToUpdate(currUser));
            model.addAttribute("principal",principal);
            model.addAttribute("errors", errorsStrings);

            return "configuration";
        }



        currUser = new User(toUpdate.getUsername(),toUpdate.getEmail(),encoder.encode(toUpdate.getNewPassword()),toUpdate.getDisplay_name(),true,currUser.getRol());
        dbmanager.changeUserInfo(currUser.getUsername(),currUser.getEmail(),currUser.getPassword(),currUser.getDisplay_name());
        return "redirect:/profile/"+toUpdate.getUsername();

    }


    @GetMapping("loginError")
    public String loginError(Model model, Principal principal){
        List<String> errorsStrings = new ArrayList<>();
        errorsStrings.add("Incorrect username or password");

        model.addAttribute( "user", new User());
        model.addAttribute("principal", principal);
        model.addAttribute("errors", errorsStrings);

        return "login";
    }

    @GetMapping("login")
    public String login(Model model, Principal principal){
        model.addAttribute( "user", new User());
        model.addAttribute("principal", principal);
        model.addAttribute("errors", new ArrayList<>());
        return "login";
    }

    @GetMapping("/")
    public String index(Model model, Principal principal){
        model.addAttribute("principal", principal);
        return "index";
    }


    @GetMapping("profile/{username}")
    public String showUser(@PathVariable String username, Model model, Principal principal) {

        try {

            model.addAttribute("notes", dbmanager.getNotesByUser(principal, username));

            model.addAttribute("user", dbmanager.getUserInfo(username));
            Map<String, Integer> map = dbmanager.getAllSocialInfo(username);
            model.addAttribute("followers", map.get("followers"));
            model.addAttribute("follows", map.get("follows"));
            model.addAttribute("noteCount", map.get("notes"));
            model.addAttribute("karma", map.get("votes"));
            model.addAttribute("emoji", calculateEmoji(map.get("votes") / 42));
            model.addAttribute("following", dbmanager.isFollowing((principal != null) ? principal.getName() : "", username));
            model.addAttribute("blocked", dbmanager.isBlocked((principal != null) ? principal.getName() : "", username));
            model.addAttribute("username",null);
        }catch (Exception e){
            //e.printStackTrace();

            model.addAttribute("username",username);
        }

        model.addAttribute("principal", principal);
        return "profile";
    }

    @PostMapping("/follow/{username}")
    public String follow(@PathVariable String username, Principal principal) {

        dbmanager.followUser(principal.getName(),username);

        return "redirect:/goBack";
    }



    @PostMapping("/delete/{noteId}")
    public String deleteNote(@PathVariable long noteId){

        dbmanager.deleteNote(noteId);

        return "redirect:/goBack";
    }

    @GetMapping("goBack")
    public String back(Principal principal, Model model) {
        model.addAttribute("principal", principal);
        Random rand = new Random();
        int n = rand.nextInt(12) + 1;
        //12 is the maximum and the 1 is our minimum.
        return "redirect:/goBack/"+n;
    }

    @GetMapping("goBack/{loadingStyle}")
    public String backStyle(@PathVariable String loadingStyle, Model model, Principal principal){
        model.addAttribute("principal", principal);
        model.addAttribute("loadingStyle", loadingStyle);
        return "goBack";
    }




    @PostMapping("/block/{username}")
    public String block(@PathVariable String username, Principal principal) {

        dbmanager.unfollowUser(principal.getName(),username);
        dbmanager.blockUser(principal.getName(),username);

        return "redirect:/goBack";
    }

    @GetMapping("users")
    public String showUsers(Model model) {
        model.addAttribute("users", dbmanager.getAllUsers());
        return "users";
    }


    @GetMapping("feed")
    public String feed(Model model, Principal principal) {

        model.addAttribute("principal", principal);
        model.addAttribute("notes",  dbmanager.getNotesFromFolloweds(principal.getName()));

        return "feed";
    }


    @GetMapping("notifications")
    public String notifigations(Model model, Principal principal){
        model.addAttribute("principal", principal);
        List<CompleteNote> notes = dbmanager.getNotifications(principal);
        model.addAttribute("notes", (notes == null)? new ArrayList<CompleteNote>() : notes);
        model.addAttribute("isNotification", true);

        return "notifications";
    }

    @GetMapping("mentions")
    public String mentions(Model model, Principal principal){
        model.addAttribute("principal", principal);
        List<CompleteNote> notes = dbmanager.getMentions(principal);
        model.addAttribute("notes", (notes == null)? new ArrayList<CompleteNote>() : notes);

        return "mentions";
    }

    @GetMapping("viewed/{noteId}")
    public String checkHasViewed(@PathVariable String noteId){
        dbmanager.updateViewed(noteId);
        return "redirect:/goBack";
    }

    @GetMapping("deleteUser")
    public String deleteUser(Principal principal, Model model){
        currUser=dbmanager.getUserInfo(principal.getName());
        model.addAttribute("principal", principal)
            .addAttribute("toUpdate", new UserToUpdate(currUser));

        return "deleteUser";
    }

    @PostMapping("deleteUser")
    public String deleteUserPost(UserToUpdate toUpdate){

        toUpdate.setUsername(currUser.getUsername());
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if(!encoder.matches(toUpdate.getPassword(),dbmanager.getPassword(toUpdate.getUsername())))
            return "redirect:/feed";

        dbmanager.deleteUser(toUpdate.getUsername());
        return "redirect:/logout";
    }


    private List<CompleteUser> parseToCompleteUser(Principal principal, List<User> users){
        List<CompleteUser> tempCompleteUserList= new ArrayList<>();

        for (User u:users) {
            Map<String, Integer> map = dbmanager.getAllSocialInfo(u.getUsername());
            int followers = map.get("followers");
            int follows = map.get("follows");
            int notesCount = map.get("notes");
            int karma = map.get("votes");
            boolean following = principal!=null? dbmanager.isFollowing((principal != null)? principal.getName():"", u.getUsername()) : false;
            boolean blocked = principal!=null? dbmanager.isBlocked((principal != null)? principal.getName():"", u.getUsername()): false;

            tempCompleteUserList.add(new CompleteUser(u,followers,follows,notesCount,following, blocked, karma));
        }

        return tempCompleteUserList;
    }

    class CompleteUser{
        public User originalUser;

        private int followers, follows, notesCount, karma;
        private boolean following, blocked;
        private String emoji;

        public User getOriginalUser() { return originalUser; }
        public int getFollowers() { return followers;}
        public int getFollows() { return follows;}
        public int getNotesCount() { return notesCount;}
        public int getKarma() { return karma;}
        public boolean isFollowing() { return following;}
        public boolean isBlocked() { return blocked;}
        public String getEmoji() {return emoji;};


        public CompleteUser(User originalUser, int followers, int follows, int notesCount, boolean following, boolean blocked, int karma) {
            this.originalUser = originalUser;
            this.followers = followers;
            this.follows = follows;
            this.notesCount = notesCount;
            this.following = following;
            this.blocked = blocked;
            this.karma = karma;
            this.emoji = calculateEmoji(karma/42);
        }
    }


}
