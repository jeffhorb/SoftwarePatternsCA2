package com.ecom.softwarepatternsca2.AuthenticationClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ecom.softwarepatternsca2.MainActivityClasses.HomePage;
import com.ecom.softwarepatternsca2.Patterns.FirebaseAuthFactory;
import com.ecom.softwarepatternsca2.Patterns.ForgotPasswordDialogBuilder;
import com.ecom.softwarepatternsca2.R;
import com.ecom.softwarepatternsca2.AppManagerClasses.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Declare UI components
    private EditText Aemail, Apassword;
    private Button loginBtn;
    private ProgressBar pBar;
    private TextView textView, forgotpword;
    private FirebaseAuth authenticate;
    private CheckBox checkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupFirebaseInstance();
        setupLoginButtonListener();
        setupSignUpNavigation();
        setupRememberMe();
        setupForgotPassword();
        setupPasswordVisibilityToggle();

    }

    // Initialize UI components
    private void initializeViews() {
        Aemail = findViewById(R.id.email);
        Apassword = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);
        pBar = findViewById(R.id.progressBar2);
        forgotpword = findViewById(R.id.forgotPassword);
        textView = findViewById(R.id.signupText);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
    }

    // Setup FirebaseAuth instance using Factory Method
    private void setupFirebaseInstance() {
        authenticate = FirebaseAuthFactory.createInstance();
    }

    // Setup login button click listener
    private void setupLoginButtonListener() {
        loginBtn.setOnClickListener(v -> {
            String mail = Aemail.getText().toString().trim();
            String pword = Apassword.getText().toString().trim();

            if (TextUtils.isEmpty(mail)) {
                Aemail.setError("Email Required");
            } else if (!mail.contains("@") || !mail.contains(".") || mail.length() <= 7) {
                Aemail.setError("Invalid Email");
            } else if (TextUtils.isEmpty(pword)) {
                Apassword.setError("Password Required");
            } else {
                loginWithEmailAndPassword(mail, pword);
            }
        });
    }

    // Perform login with email and password
    private void loginWithEmailAndPassword(String mail, String pword) {
        pBar.setVisibility(View.VISIBLE);
        LoginHandler.handleLogin(authenticate, mail, pword, pBar, checkBoxRememberMe, this);
    }

    // Setup password visibility toggle
    private void setupPasswordVisibilityToggle() {
        ToggleButton showPasswordToggle = findViewById(R.id.showPasswordToggle);
        showPasswordToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int inputType = isChecked ?
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            Apassword.setInputType(inputType);
            Apassword.setSelection(Apassword.length());
        });
        // Reset password input type on focus change
        Apassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Apassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }

    // Setup navigation to sign up activity
    private void setupSignUpNavigation() {
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Setup remember me functionality
    private void setupRememberMe() {
        if (!new SharedPrefManager(this).isUserLogedOut()) {
            Intent intent = new Intent(LoginActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    // Setup forgot password functionality
    private void setupForgotPassword() {
        forgotpword.setOnClickListener(v -> ForgotPasswordDialogBuilder.showRecoverPasswordDialog(this, authenticate));
    }
}
