package com.d4vinci.firebaseapplication;

import com.google.firebase.auth.FirebaseUser;

public class AuthState {
    private FirebaseUser firebaseUser;

    public AuthState(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }
}
