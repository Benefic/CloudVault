package ru.abenefic.cloudvault.server.support;

import org.hibernate.Session;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.common.auth.AuthorisationException;
import ru.abenefic.cloudvault.server.model.User;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UserService {

    //++ заглушка для Code review
    private static final ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>();
    private static final boolean useMock = true;
    //--

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

        if (useMock) {
            //++ заглушка для Code review
            User mockUser;
            for (User usr : users) {
                if (usr.getLogin().equals(auth.getLogin())) {
                    throw new AuthorisationException("Login is busy");
                }
            }
            mockUser = new User(auth.getLogin(), auth.getPassword());
            users.add(mockUser);
            mockUser.setId(users.size());
            return mockUser;
            //--
        }
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

        if (useMock) {
            //++ заглушка для Code review
            User mockUser;
            for (User usr : users) {
                if (usr.getLogin().equals(auth.getLogin())) {
                    return usr;
                }
            }
            mockUser = new User(auth.getLogin(), auth.getPassword());
            users.add(mockUser);
            mockUser.setId(users.size());
            return mockUser;
            //--
        }

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
