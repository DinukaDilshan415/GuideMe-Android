package me.dinuka.guideme.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import androidx.credentials.CredentialManagerCallback; // The correct one

import java.util.concurrent.Executors; // To handle the background task

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityLoginBinding;
import taimoor.sultani.sweetalert2.Sweetalert;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    CredentialManager credentialManager = CredentialManager.create(this);
    private FirebaseAuth firebaseAuth;
    GetCredentialRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.gotoRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("904896597419-jf6077q5tgdeo5517erilsdeh40v9tri.apps.googleusercontent.com")
                .build();

        request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        binding.btnGoogle.setOnClickListener(v -> {
            startGoogleSignIn();
        });

        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.inputLoginEmail.getText().toString().trim();
            String password = binding.inputLoginPassword.getText().toString().trim();

            if (email.isEmpty()){
                binding.inputLoginEmail.setError("Email is required");
                binding.inputLoginEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.inputLoginEmail.setError("Please provide a valid email");
                binding.inputLoginEmail.requestFocus();
                return;
            }

            if (password.isEmpty()){
                binding.inputLoginPassword.setError("Password is required");
                binding.inputLoginPassword.requestFocus();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if (task.isSuccessful()){
                                updateUI(firebaseAuth.getCurrentUser());
                            } else {
                                new Sweetalert(LoginActivity.this, Sweetalert.WARNING_TYPE)
                                        .setTitleText("Warning")
                                        .setContentText("Incorrect Email Or Passowrd")
                                        .show();
//                                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                            }
                        }
                    });
        });

    }

    public void startGoogleSignIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            credentialManager.getCredentialAsync(
                    this,
                    request,
                    null, // CancellationSignal
                    Executors.newSingleThreadExecutor(), // The library needs an Executor in Java
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleSignIn(result.getCredential());
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                            Log.e("Auth", "Google Sign-In Error: " + e.getMessage());
                        }
                    }
            );
        }
    }

    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential &&
                credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            GoogleIdTokenCredential googleIdToken = GoogleIdTokenCredential.createFrom(credential.getData());
            String idToken = googleIdToken.getIdToken();
            String email = googleIdToken.getId();

            // Successfully signed in! Navigate to GuideMe Home Screen
            Log.d("Auth", "Welcome " + email);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.e("Auth", "Google Sign-In Error: Invalid Credential Type");
        }
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}