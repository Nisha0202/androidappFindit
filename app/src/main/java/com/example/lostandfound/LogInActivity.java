package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {
    private EditText etStId;
    private EditText etPassword;
    private Button btnSaveLogIn;
    private CheckBox cbRememberMe;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("RememberMe", false);

        String stId = prefs.getString("StId", "");
        String password = prefs.getString("Password", "");
        etStId = findViewById(R.id.etStId);
        etPassword = findViewById(R.id.etPassword);
        btnSaveLogIn = findViewById(R.id.btnSaveLogIn);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        // Autofill the fields if "Remember Me" is checked
        if (rememberMe) {
            etStId.setText(stId);
            etPassword.setText(password);
            cbRememberMe.setChecked(true);
        }

        btnSaveLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stId = etStId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(password.isEmpty() || stId.isEmpty()){
                    showDialog("Errors", "Please fill all the fields");
                } else {
                    checkUser(stId, password);
                }
            }
        });
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void checkUser(String stId, String pass) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("stId").equalTo(stId);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child(stId).child("password").getValue(String.class);
                    if (passwordFromDB.equals(pass)) {
                        String emailFromDB = snapshot.child(stId).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(stId).child("username").getValue(String.class);

                        // Get the current user and reload their data
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful() && user.isEmailVerified()) {
                                        // the "Remember Me" preference
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("RememberMe", cbRememberMe.isChecked());
                                        editor.putString("Username", usernameFromDB); // storing must info
                                        editor.putString("Email", emailFromDB);
                                        if (cbRememberMe.isChecked()) {
                                            editor.putString("StId", stId);
                                            editor.putString("Password", pass); // Save the password if "Remember Me" is checked
                                            editor.putBoolean("IsLoggedIn", true);
                                        } else {
                                            editor.remove("StId");
                                            editor.remove("Password");
                                        }
                                        editor.apply();

                                        Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                                        Toast.makeText(LogInActivity.this, "You have logged in successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        showDialog("Email Verification", "Please verify your email before logging in.");
                                    }
                                }
                            });
                        } else {
                            showDialog("Errors", "User does not exist.");
                        }
                    } else {
                        showDialog("Errors", "Invalid Credentials");
                    }
                } else {
                    showDialog("Errors", "User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors.
            }
        });
    }


}



