package com.d4vinci.firebaseapplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.reactivestreams.Publisher;

import java.util.ArrayList;

import io.reactivex.Flowable;

import static com.d4vinci.firebaseapplication.FirebaseTools.fromRef;

public class DataService {

    public static Flowable<ArrayList<String>> getStuff() {
        DatabaseReference keysRef = FirebaseDatabase.getInstance().getReference("keys");
        return fromRef(keysRef)
                .map(dataSnapshot -> {
                    ArrayList<String> keys = new ArrayList<>();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            keys.add(child.getKey());
                        }
                    }
                    return keys;
                })
                .switchMap(keys -> {
                    ArrayList<Publisher<String>> publishers = new ArrayList<>();
                    for (String key : keys) {
                        publishers.add(getValue(key));
                    }
                    return Flowable.combineLatest(publishers, objects -> {
                        ArrayList<String> values = new ArrayList<>();
                        for (Object object : objects) {
                            values.add((String) object);
                        }
                        return values;
                    });
                });
    }

    public static Publisher<String> getValue(String key) {
        return fromRef(FirebaseDatabase.getInstance().getReference("values").child(key).child("text"))
                .map(dataSnapshot -> dataSnapshot.getValue(String.class));
    }

}
