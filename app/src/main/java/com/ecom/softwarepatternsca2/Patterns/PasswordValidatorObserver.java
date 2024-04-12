package com.ecom.softwarepatternsca2.Patterns;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;


//PasswordValidatorObserver observes using Observer Pattern changes in the password field and updates the UI components accordingly, promoting modularity and flexibility.
public class PasswordValidatorObserver implements TextWatcher {
    private EditText password;
    private TextView num, atoz, AtoZ, symbols;

    public PasswordValidatorObserver(EditText password, TextView num, TextView atoz, TextView AtoZ, TextView symbols) {
        this.password = password;
        this.num = num;
        this.atoz = atoz;
        this.AtoZ = AtoZ;
        this.symbols = symbols;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String pword = password.getText().toString().trim();
        validatePassword(pword);
    }

    @Override
    public void afterTextChanged(Editable s) {}

    private void validatePassword(String password) {
        // Password validation logic goes here
        String specialC = ("[ \\\\@  [\\\"]\\\\[\\\\]\\\\\\|^{#%'*/<()>}:`;,!& .?_$+-]+");
        // check for pattern
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern specialChar = Pattern.compile(specialC);

        // if lowercase character is not present
        if (!lowercase.matcher(password).find()) {
            atoz.setTextColor(Color.RED);
        } else {
            // if lowercase character is  present
            atoz.setTextColor(Color.GREEN);
        }

        // if uppercase character is not present
        if (!uppercase.matcher(password).find()) {
            AtoZ.setTextColor(Color.RED);
        } else {
            // if uppercase character is  present
            AtoZ.setTextColor(Color.GREEN);
        }
        // if digit is not present
        if (!digit.matcher(password).find()) {
            num.setTextColor(Color.RED);
        } else {
            // if digit is present
            num.setTextColor(Color.GREEN);
        }
        // if password symbol is present
        if (!specialChar.matcher(password).find()) {
            symbols.setTextColor(Color.RED);
        } else {
            symbols.setTextColor(Color.GREEN);
        }


    }
}

