package ru.abenefic.cloudvault.common.auth;

import ru.abenefic.cloudvault.common.NetworkCommand;

/**
 * Исключение, на случай какой-то ошибки при авторизации или регистрации
 */
public class AuthorisationException extends Exception implements NetworkCommand {
    public AuthorisationException(String message) {
        super(message);
    }
}
