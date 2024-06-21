package com.example.lostandfound;


import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;


// GoogleLogin.java
public class GoogleLogin extends AppCompatActivity {
    private static final String TAG = "GoogleLogin";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.server_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                PendingIntent pendingIntent = result.getPendingIntent();
                                try {
                                    startIntentSenderForResult(pendingIntent.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(TAG, "Could not start sign in", e);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to begin sign in", e);
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(data);
        }
    }

    private void handleSignInResult(Intent data) {
        try {
            SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = googleCredential.getGoogleIdToken();
            if (idToken != null) {
                authenticateWithFirebase(idToken);
            } else {
                Log.w(TAG, "No ID token received");
            }
        } catch (ApiException e) {
            Log.e(TAG, "Error getting sign in credential", e);
        }
    }

    private void authenticateWithFirebase(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(GoogleLogin.this, HomeActivity.class));
        } else {
            // Show an error message or retry button
        }
    }
}