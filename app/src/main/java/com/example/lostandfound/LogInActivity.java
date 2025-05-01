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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;


public class LogInActivity extends AppCompatActivity {
    private EditText etEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText etPassword;
    private Button btnSaveLogIn;
    private CheckBox cbRememberMe;
    private SharedPreferences prefs;
    private TextView fpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("RememberMe", false);
        mAuth = FirebaseAuth.getInstance();

        String stId = prefs.getString("StId", "");
        String password = prefs.getString("Password", "");
        etEmail= findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSaveLogIn = findViewById(R.id.btnSaveLogIn);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        fpass = findViewById(R.id.forgetPass);

        // Autofill the fields if "Remember Me" is checked
        if (rememberMe) {
            etEmail.setText(stId);
            etPassword.setText(password);
            cbRememberMe.setChecked(true);
        }

        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnSaveLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (password.isEmpty() || emailId.isEmpty()) {
                    showDialog("Errors", "Please fill all the fields");
                } else {
                    checkUser(emailId, password);
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

    public void checkUser(String emailId, String pass) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(emailId);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {  // Iterate through results
                        String emailFromDB = userSnapshot.child("email").getValue(String.class);
                        String usernameFromDB = userSnapshot.child("username").getValue(String.class);

                        loginUser(emailFromDB, pass, usernameFromDB);
                        return;
                    }
                } else {
                    showDialog("Errors", "User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showDialog("Errors", "Database error: " + error.getMessage());
            }
        });
    }

    private void loginUser(String email, String password, String username) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> reloadTask) {
                                        if (reloadTask.isSuccessful() && user.isEmailVerified()) {
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putBoolean("RememberMe", cbRememberMe.isChecked());
                                            editor.putString("Email", email);
                                            editor.putString("Username", username); // Always store the username
                                            if (cbRememberMe.isChecked()) {
                                                editor.putString("StId", email);
                                                editor.putString("Password", password);
                                                editor.putBoolean("IsLoggedIn", true);
                                            } else {
                                                editor.remove("StId");
                                                editor.remove("Password");
                                            }
                                            editor.apply();

                                            Toast.makeText(LogInActivity.this, "You have logged in successfully!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
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
                            showDialog("Errors", "Login failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

}



