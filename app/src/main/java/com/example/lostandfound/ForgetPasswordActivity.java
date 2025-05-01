package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail;
    private Button btnSavePass, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Link UI components
        etEmail = findViewById(R.id.etEmail);

        btnSavePass = findViewById(R.id.btnSavePass);
        btnReturn = findViewById(R.id.btnReturn);

        // Set Click Listener
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        // Set Click Listener
        btnSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    changePassword();




            }
        });
    }

    private void changePassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgetPasswordActivity.this, "Password reset email sent. Check your inbox!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
