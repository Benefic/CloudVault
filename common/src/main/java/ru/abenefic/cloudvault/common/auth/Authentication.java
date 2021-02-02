package ru.abenefic.cloudvault.common.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.abenefic.cloudvault.common.NetworkCommand;

/**
 * Этот класс отвечает как за авторизацию, так и за регистрацию
 * переключатель режима - флаг registration
 */


@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Authentication implements NetworkCommand {
    @NonNull
    private String login;
    @NonNull
    private String password;
    private String token;
    private boolean registration;
    private int userId;
}
