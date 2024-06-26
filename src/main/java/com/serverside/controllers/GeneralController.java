package com.serverside.controllers;
import com.serverside.Persist;
import com.serverside.entities.*;
import com.serverside.responses.BasicResponse;
import com.serverside.responses.LoginResponse;
import com.serverside.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.List;
import static com.serverside.utils.Errors.*;



@RestController
public class GeneralController {
    private final List<SseEmitter> clients = new ArrayList<>();

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;

    @PostConstruct
    public void init() {
//        new Thread(() -> {
//            while (true) {
//                // Start of a season
//                initializeSeason();
//                runSeason();
//                endSeason();
//                pauseBetweenSeasons();
//            }
//        }).start();
    }

    @RequestMapping(value = "/start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter startStream (){
        SseEmitter sseEmitter = new SseEmitter((long)(10*60*1000));
        clients.add(sseEmitter);
        return sseEmitter;
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String email, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        if (email != null && email.length() > 0) {
            if (password != null && password.length() > 0) {
                User user = persist.login(email,password);
                if (user != null) {
                    int id = dbUtils.getUserId(email, password);
                    user.setId(id);
                    basicResponse = new LoginResponse(true, errorCode, user.getId());
                } else {
                    errorCode = ERROR_LOGIN_WRONG_CREDS;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_EMAIL;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }

    @RequestMapping(value = "add-user")
    public boolean addUser(String username, String password, String email) {
        boolean success = false;
        User userToAdd = new User(username, password, email);
        success = dbUtils.addUser(userToAdd);
        userToAdd.setId(dbUtils.getUserId(email,password));
        return success;
    }


    @RequestMapping(value = "get-users")
    public List<User> getUsers() {
        return dbUtils.getAllUsers();
    }

}