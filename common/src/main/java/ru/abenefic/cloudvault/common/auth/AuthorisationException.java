package ru.abenefic.cloudvault.common.auth;

import ru.abenefic.cloudvault.common.NetworkCommand;

public class AuthorisationException extends Exception implements NetworkCommand {
    public AuthorisationException(String message) {
        super(message);
    }
}
