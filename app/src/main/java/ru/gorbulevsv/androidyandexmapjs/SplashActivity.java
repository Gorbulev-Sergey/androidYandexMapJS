package ru.gorbulevsv.androidyandexmapjs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth auth;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        ifLoginIsSaved();
    }

    void ifLoginIsSaved() {
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");
        if (!email.isEmpty() && !password.isEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) startActivity(new Intent(this, MainActivity.class));
            });
        }
        else{
            startActivity(new Intent(this, RegisterLoginActivity.class));
        }
    }
}