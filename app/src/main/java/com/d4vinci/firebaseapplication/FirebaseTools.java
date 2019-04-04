package com.d4vinci.firebaseapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

public class FirebaseTools {
    public static Flowable<AuthState> getAuthState(FirebaseAuth firebaseAuth) {
        return Flowable.create(emitter -> {
            emitter.onNext(new AuthState(firebaseAuth.getCurrentUser()));
            FirebaseAuth.AuthStateListener listener = authState -> emitter.onNext(new AuthState(authState.getCurrentUser()));
            firebaseAuth.addAuthStateListener(listener);
            emitter.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    firebaseAuth.removeAuthStateListener(listener);
                }

                @Override
                public boolean isDisposed() {
                    return false;
                }
            });
        }, BackpressureStrategy.LATEST);//.delay(0, TimeUnit.MILLISECONDS).cast(AuthState.class);
    }

    public static Flowable<DataSnapshot> fromRef(DatabaseReference ref) {
        return Flowable.create(
                emitter -> {
                    ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            emitter.onNext(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            emitter.onError(new Throwable(databaseError.toException()));
                        }
                    };
                    ref.addValueEventListener(listener);
                    emitter.setDisposable(new Disposable() {
                        @Override
                        public void dispose() {
                            ref.removeEventListener(listener);
                        }

                        @Override
                        public boolean isDisposed() {
                            return false;
                        }
                    });
                }, BackpressureStrategy.LATEST
        );
//                .delay(0, TimeUnit.MILLISECONDS)
//                .cast(DataSnapshot.class);
    }
}
