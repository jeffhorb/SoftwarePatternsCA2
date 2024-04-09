package com.ecom.softwarepatternsca2;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String KEY_DARK_MODE = "DarkMode";
    Context context;


    SharedPrefManager(Context context) {
        this.context = context;
    }
    public void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.commit();
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }
    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
        return isEmailEmpty || isPasswordEmpty;
    }

    public void clearSession() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Email");
        editor.remove("Password");
        editor.apply();
    }

    // Save Dark Mode Preference
    public void saveDarkMode(boolean darkModeEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("darkMode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DARK_MODE, darkModeEnabled);
        editor.apply();
    }

    // Retrieve Dark Mode Preference
    public boolean isDarkModeEnabled() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("darkMode", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

}

