package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnSave;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                btnLogin = findViewById(R.id.btnLogin);
                btnSave = findViewById(R.id.btnSave);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start LoginActivity
                        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start SignUpActivity
                        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                });
            }

        }

