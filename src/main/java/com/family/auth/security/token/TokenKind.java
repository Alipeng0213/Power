package com.family.auth.security.token;

public enum TokenKind {
    USER, CLIENT;

    public int getCode() {
        switch (this) {
            case USER:
                return 1;
            case CLIENT:
                return 2;
            default:
                // maybe throw exception better?
                return 3;
        }
    }
}