package com.HabitTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etName;
    TextInputLayout tilName;
    Button btnTabLogin, btnTabSignup, btnSubmit;
    TextView tvError;
    SharedPreferences prefs;
    boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);

        // TEMP: always show login screen for testing
        if (false) {
            goToMain();
            return;
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        tilName = findViewById(R.id.til_name);
        btnTabLogin = findViewById(R.id.btn_tab_login);
        btnTabSignup = findViewById(R.id.btn_tab_signup);
        btnSubmit = findViewById(R.id.btn_submit);
        tvError = findViewById(R.id.tv_error);

        btnTabLogin.setOnClickListener(v -> switchMode(true));
        btnTabSignup.setOnClickListener(v -> switchMode(false));
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    void switchMode(boolean loginMode) {
        isLoginMode = loginMode;
        if (loginMode) {
            // Login mode
            tilName.setVisibility(View.GONE);
            btnSubmit.setText("Login");
            btnTabLogin.setBackgroundTintList(
                    getColorStateList(R.color.dark_green));
            btnTabLogin.setTextColor(getColor(R.color.beige));
            btnTabSignup.setBackgroundTintList(
                    getColorStateList(R.color.surface_primary));
            btnTabSignup.setTextColor(getColor(R.color.text_secondary));
        } else {
            // Sign up mode
            tilName.setVisibility(View.VISIBLE);
            btnSubmit.setText("Sign Up");
            btnTabSignup.setBackgroundTintList(
                    getColorStateList(R.color.dark_green));
            btnTabSignup.setTextColor(getColor(R.color.beige));
            btnTabLogin.setBackgroundTintList(
                    getColorStateList(R.color.surface_primary));
            btnTabLogin.setTextColor(getColor(R.color.text_secondary));
        }
        tvError.setVisibility(View.GONE);
    }

    void handleSubmit() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (email.isEmpty()) {
            showError("Please enter your email");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (isLoginMode) {
            // Check saved credentials
            String savedEmail = prefs.getString("email", "");
            String savedPassword = prefs.getString("password", "");

            if (email.equals(savedEmail) && password.equals(savedPassword)) {
                loginSuccess();
            } else {
                showError("Incorrect email or password");
            }
        } else {
            // Sign up — save credentials
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                showError("Please enter your name");
                return;
            }
            prefs.edit()
                    .putString("email", email)
                    .putString("password", password)
                    .putString("name", name)
                    .apply();
            loginSuccess();
        }
    }

    void loginSuccess() {
        prefs.edit().putBoolean("is_logged_in", true).apply();

        if (!isLoginMode) {
            // Just signed up → go to Profile Setup
            startActivity(new Intent(this, ProfileSetupActivity.class));
        } else {
            // Logging in → check if profile is done
            if (prefs.getBoolean("profile_setup_done", false)) {
                // Profile done → go to Welcome/Dashboard
                goToMain();
            } else {
                // Profile not done → go to Profile Setup
                startActivity(new Intent(this, ProfileSetupActivity.class));
            }
        }
        finish();
    }

    void goToMain() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
