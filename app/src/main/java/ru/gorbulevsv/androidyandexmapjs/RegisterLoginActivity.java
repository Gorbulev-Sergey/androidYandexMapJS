package ru.gorbulevsv.androidyandexmapjs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterLoginActivity extends AppCompatActivity {
    EditText editEmail, editPassword;
    Button buttonLogin, buttonRegister;
    FirebaseAuth auth;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        initControls();
        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
    }

    void initControls() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(view -> {
            auth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    preferences.edit().putString("email", editEmail.getText().toString()).apply();
                    preferences.edit().putString("password", editPassword.getText().toString()).apply();
                    Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    Toast.makeText(this, "Ошибка входа!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(view -> {
            auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    preferences.edit().putString("email", editEmail.getText().toString()).apply();
                    preferences.edit().putString("password", editPassword.getText().toString()).apply();
                    Toast.makeText(this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    Toast.makeText(this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}