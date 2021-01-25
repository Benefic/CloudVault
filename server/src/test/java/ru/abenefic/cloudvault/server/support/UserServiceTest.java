package ru.abenefic.cloudvault.server.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.abenefic.cloudvault.common.auth.Authentication;
import ru.abenefic.cloudvault.server.model.User;

class UserServiceTest {

    @Test
    void register() {
        Authentication auth = new Authentication("user" + 1, "123456789");
        try {
            User user = UserService.instance().register(auth);
            Assertions.assertNotNull(user);
        } catch (Exception e) {
            // логгер ту ни к чему

            e.printStackTrace();
        }
    }

    @Test
    void authorize() {
        Authentication auth = new Authentication("user" + 1, "123456789");
        try {
            User user = UserService.instance().authorize(auth);
            Assertions.assertNotNull(user);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.assertTrue(false);
            // логгер ту ни к чему
        }

    }
}