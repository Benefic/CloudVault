package ru.abenefic.cloudvault.common.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Authentication implements Serializable {
    @NonNull
    private String login;
    @NonNull
    private String password;
    private boolean registration;
    private boolean success;
    private String reason;
}
