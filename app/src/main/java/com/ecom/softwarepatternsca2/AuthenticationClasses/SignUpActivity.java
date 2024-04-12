package com.ecom.softwarepatternsca2.AuthenticationClasses;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ecom.softwarepatternsca2.Patterns.AlertDialogBuilder;
import com.ecom.softwarepatternsca2.Patterns.FirebaseAuthFactory;
import com.ecom.softwarepatternsca2.Patterns.FirebaseFirestoreFactory;
import com.ecom.softwarepatternsca2.Patterns.PasswordValidatorObserver;
import com.ecom.softwarepatternsca2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    // Declare UI components
    private EditText mail, password;
    private TextView num, atoz, AtoZ, symbols;
    private Button signupButn;
    private ProgressBar pBar;
    private FirebaseAuth authenticate;
    private FirebaseFirestore db;
    private AlertDialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        setupFirebaseInstances();
        setupPasswordValidationObserver();
        setupSignUpButtonListener();
        setupLoginNavigation();
        setupPasswordVisibilityToggle();

    }

    // Initialize UI components
    private void initializeViews() {
        mail = findViewById(R.id.email);
        password = findViewById(R.id.password);
        num = findViewById(R.id.num);
        atoz = findViewById(R.id.atoz);
        AtoZ = findViewById(R.id.AtoZ);
        symbols = findViewById(R.id.symbol);
        signupButn = findViewById(R.id.signupButton);
        pBar = findViewById(R.id.progressBar);
    }

    // Setup FirebaseAuth and FirebaseFirestore instances using Factory Method
    private void setupFirebaseInstances() {
        authenticate = FirebaseAuthFactory.createInstance();
        db = FirebaseFirestoreFactory.createInstance();
    }

    // Setup password validation observer using Observer Pattern
    private void setupPasswordValidationObserver() {
        password.addTextChangedListener(new PasswordValidatorObserver(password, num, atoz, AtoZ, symbols));
    }

    // Setup sign up button click listener
    private void setupSignUpButtonListener() {
        signupButn.setOnClickListener(view -> {
            String email = mail.getText().toString().trim();
            String pword = password.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mail.setError("Email Required");
            } else if (!email.contains("@") || !email.contains(".") || email.length() <= 7) {
                mail.setError("Invalid Email");
            } else if (TextUtils.isEmpty(pword)) {
                password.setError("Password Required");
            } else if (pword.length() < 8) {
                password.setError("Password length must be at least 8 characters");
            } else {
                showUserDetailsDialog(email, pword);
            }
        });
    }

    // Setup password visibility toggle
    private void setupPasswordVisibilityToggle() {
        ToggleButton showPasswordToggle = findViewById(R.id.showPasswordToggle);
        showPasswordToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int inputType = isChecked ?
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            password.setInputType(inputType);
            password.setSelection(password.length());
        });

        // Reset password input type on focus change
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }

    // Show dialog for entering user details
    private void showUserDetailsDialog(String email, String password) {
        dialogBuilder = AlertDialogBuilder.createInstance(this);
        AlertDialog dialog = dialogBuilder.buildUserDetailsDialog(email, password, this::signUpWithEmailPassword);
        dialog.show();
    }

    // Perform sign up with email and password
    private void signUpWithEmailPassword(String email, String password, String customerName, String address1, String address2, String address3, String eircode) {
        pBar.setVisibility(View.VISIBLE);
        SignUpHandler.handleSignUp(authenticate, email, password, customerName, address1, address2, address3, eircode, pBar, db, this);
    }

    // Setup navigation to login activity
    private void setupLoginNavigation() {
        TextView loginNow = findViewById(R.id.loginNow);
        loginNow.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
