package ru.abenefic.cloudvault.server.support;

import org.hibernate.Session;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;
import ru.abenefic.cloudvault.server.model.User;

public class UserService {

    private static UserService instance;

    private UserService() {
    }

    public static UserService instance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User register(Authentication auth) throws Exception {
        // check login busy
        Session session = Database.instance().openSession();
        User user = session
                .byNaturalId(User.class)
                .using("login", auth.getLogin())
                .load();
        if (user != null) {
            throw new AuthorisationException("Login is busy");
        }
        // create user and return
        user = new User(auth.getLogin(), auth.getPassword());
        int id = (int) Database.saveInTransaction(user);
        user.setId(id);
        return user;
    }

    public User authorize(Authentication auth) throws Exception {
        Session session = Database.instance().openSession();
        User user = session
                .byNaturalId(User.class)
                .using("login", auth.getLogin())
                .load();
        if (user == null) {
            throw new AuthorisationException("User not found!");
        } else if (!user.getPassword().equals(auth.getPassword())) {
            throw new AuthorisationException("Incorrect password!");
        } else {
            return user;
        }

    }
}
