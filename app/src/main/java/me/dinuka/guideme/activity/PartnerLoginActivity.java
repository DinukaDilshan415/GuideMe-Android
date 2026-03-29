package me.dinuka.guideme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityPartnerLoginBinding;
import me.dinuka.guideme.model.User;
import taimoor.sultani.sweetalert2.Sweetalert;

public class PartnerLoginActivity extends AppCompatActivity {

    private ActivityPartnerLoginBinding binding;
    CredentialManager credentialManager = CredentialManager.create(this);
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    GetCredentialRequest request;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartnerLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.gotoPartnerRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerLoginActivity.this, PartnerRegisterActivity.class);
            startActivity(intent);
            finish();
        });

        binding.loginSwitchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerLoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.textForgotPassword.setOnClickListener(v -> {
            if (!binding.inputPartnerLoginEmail.getText().toString().trim().isEmpty()) {
                firebaseAuth.sendPasswordResetEmail(binding.inputPartnerLoginEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    new Sweetalert(PartnerLoginActivity.this, Sweetalert.SUCCESS_TYPE)
                                            .setTitleText("Success")
                                            .setContentText("Password reset email sent to your email.")
                                            .show();
                                    Log.d(TAG, "Email sent.");
                                }
                            }
                        });
            } else {
                binding.emailInput.setError("Please enter your email");
                binding.emailInput.requestFocus();
            }
        });

        binding.partnerLoginBtn.setOnClickListener(v -> {
            String email = binding.inputPartnerLoginEmail.getText().toString().trim();
            String password = binding.inputPartnerLoginPassword.getText().toString().trim();

            if (email.isEmpty()) {
                binding.inputPartnerLoginEmail.setError("Email is required");
                binding.inputPartnerLoginEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.inputPartnerLoginEmail.setError("Please provide a valid email");
                binding.inputPartnerLoginEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.inputPartnerLoginPassword.setError("Password is required");
                binding.inputPartnerLoginPassword.requestFocus();
                return;
            }

            firebaseFirestore.collection("partners")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!ds.isEmpty()) {
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                updateUI(firebaseAuth.getCurrentUser());
                                            } else {
                                                new Sweetalert(PartnerLoginActivity.this, Sweetalert.WARNING_TYPE)
                                                        .setTitleText("Warning")
                                                        .setContentText("Incorrect Email Or Passowrd")
                                                        .show();
                                            }
                                        }
                                    });
                        } else {
                            new Sweetalert(PartnerLoginActivity.this, Sweetalert.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Incorrect Email Or Passowrd. Try Again")
                                    .show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("PartnerLoginActivity", "Error getting users", e);
                    });
        });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(PartnerLoginActivity.this, PartnerMainActivity.class);
        startActivity(intent);
        finish();
    }
}