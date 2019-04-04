package com.d4vinci.firebaseapplication;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import static com.d4vinci.firebaseapplication.DataService.getStuff;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Disposable stuffSubscription;

    // Views
    private Button btnLogin;
    private Button btnLogout;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views Binding
        btnLogin = findViewById(R.id.btn_login);
        btnLogout = findViewById(R.id.btn_logout);
        tvMessage = findViewById(R.id.tvMessage);

        btnLogin.setOnClickListener(v -> FirebaseAuth.getInstance().signInAnonymously());
        btnLogout.setOnClickListener(v -> FirebaseAuth.getInstance().signOut());
    }

    @Override
    protected void onStart() {
        super.onStart();
        stuffSubscription = FirebaseTools.getAuthState(FirebaseAuth.getInstance())
                .switchMap(authState -> {
                    if (authState.getFirebaseUser() == null) {
                        return Flowable.just("You should be authenticated to see this!!");
                    } else {
                        return getStuff()
                                .map(values -> {
                                    StringBuilder concatS = new StringBuilder();
                                    for (String value : values) {
                                        concatS.append(value).append(", ");
                                    }
                                    return concatS.toString();
                                });
                    }
                })
                .subscribe(concatS -> tvMessage.setText(concatS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (stuffSubscription != null) {
            stuffSubscription.dispose();
        }
    }

}
