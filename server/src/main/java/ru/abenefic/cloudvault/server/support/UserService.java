package ru.abenefic.cloudvault.server.support;

import org.hibernate.Session;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.server.model.User;

public class UserService {

    public User register(Authentication auth) throws Exception {
        // check login busy
        Session session = Database.instance().openSession();
        User user = session
                .byNaturalId(User.class)
                .using("login", auth.getLogin())
                .load();
        if (user != null) {
            throw new AuthorizationException("Login is busy");
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
            throw new AuthorizationException("User not found!");
        } else if (!user.getPassword().equals(auth.getPassword())) {
            throw new AuthorizationException("Incorrect password!");
        } else {
            return user;
        }

    }
}
