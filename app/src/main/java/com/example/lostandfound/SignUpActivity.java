package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword, etRePassword;
    private CheckBox cbRememberMe;
    private Button btnSaveSignUp;
    private String error = "";
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnSaveSignUp = findViewById(R.id.btnSaveSignUp);
//        etStId = findViewById(R.id.etStId);

//        // Automatic stid
//        etEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String email = etEmail.getText().toString().trim();
//                if (email.contains("@")) {
//                    String emailStId = email.substring(0, email.indexOf("@"));
//                    etStId.setText(emailStId);
//                }
//            }
//        });

        btnSaveSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String re_password = etRePassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
//                String stId = etStId.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() /*|| stId.isEmpty()*/) {
                    showDialog("Errors", "Please fill all the fields");
                    return;
                }

                if (!password.equals(re_password)) {
                    error = error + "Passwords don't match\n";
                }

                if (username.length() > 15) {
                    error = error + "Username cannot be so Long \n";
                }
                if (password.length() < 4) {
                    error = error + "Password must be more than 4 characters\n";
                }
                if (!isValidEmail(email)) {
                    error = error + "Please use a valid email.";
                }
                if (error.length() > 0) {
                    showDialog("Errors", error);
                    error = "";
                } else {
                    // Check if a user with the same email already exists
                    reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // If a user with the same email already exists, show an error message
                                showDialog("Errors", "A user with this email already exists");
                            } else {
                                // Create a new user in Firebase Authentication
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Send verification email
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    if (user != null) {
                                                        user.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(SignUpActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(SignUpActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                    // Save the user to the database
                                                    User newUser = new User(email, username);
                                                    reference.child(user.getUid()).setValue(newUser);

                                                    // Save the "Remember Me" setting
                                                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putString("Username", username); // Always store the username
                                                    editor.putString("Email", email);

                                                    if (cbRememberMe.isChecked()) {
                                                        editor.putBoolean("RememberMe", true);
                                                        editor.putString("StId", email);
                                                        editor.putBoolean("IsLoggedIn", true);
                                                    } else {
                                                        editor.putBoolean("RememberMe", false);
                                                        editor.remove("StId");
                                                        editor.remove("Password");
                                                    }
                                                    editor.apply();

                                                    Toast.makeText(SignUpActivity.this, "You have signed up successfully!", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    // Handle errors
                                                    String errorMessage = "Authentication failed.";
                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthWeakPasswordException e) {
                                                        errorMessage = "Weak password. Please provide a stronger password.";
                                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                                        errorMessage = "Invalid email format.";
                                                    } catch (FirebaseAuthUserCollisionException e) {
                                                        errorMessage = "This email is already registered.";
                                                    } catch (Exception e) {
                                                        errorMessage = e.getMessage();
                                                    }
                                                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
}