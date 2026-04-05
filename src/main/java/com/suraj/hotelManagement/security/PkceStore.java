package com.suraj.hotelManagement.security;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PkceStore {

    private Map<String, String> codeChallengeStore = new HashMap<>();
    private Map<String, String> authCodeUserStore = new HashMap<>();

    // store challenge
    public void storeCodeChallenge(String authCode, String codeChallenge) {
        codeChallengeStore.put(authCode, codeChallenge);
    }

    // store user
    public void storeUser(String authCode, String username) {
        authCodeUserStore.put(authCode, username);
    }

    public String getCodeChallenge(String authCode) {
        return codeChallengeStore.get(authCode);
    }

    public String getUsername(String authCode) {
        return authCodeUserStore.get(authCode);
    }

    public void remove(String authCode) {
        codeChallengeStore.remove(authCode);
        authCodeUserStore.remove(authCode);
    }
}