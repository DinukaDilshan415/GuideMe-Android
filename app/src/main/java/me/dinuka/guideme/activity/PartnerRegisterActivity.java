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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityPartnerRegisterBinding;
import me.dinuka.guideme.model.User;
import me.dinuka.guideme.util.RegexValidator;
import taimoor.sultani.sweetalert2.Sweetalert;

public class PartnerRegisterActivity extends AppCompatActivity {

    private ActivityPartnerRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPartnerRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gotoPartnerLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerRegisterActivity.this, PartnerLoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.registerSwitchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.partnerRegisterBtn.setOnClickListener(v -> {
            String name = binding.inputPartnerRegisterName.getText().toString().trim();
            String email = binding.inputPartnerRegisterEmail.getText().toString().trim();
            String password = binding.inputPartnerRegisterPassword.getText().toString().trim();
            String reTypePassword = binding.inputPartnerRegisterReTypePassword.getText().toString().trim();

            if (name.isEmpty()) {
                binding.inputPartnerRegisterName.setError("Name is required");
                binding.inputPartnerRegisterName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                binding.inputPartnerRegisterEmail.setError("Email is required");
                binding.inputPartnerRegisterEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.inputPartnerRegisterEmail.setError("Please provide a valid email");
                binding.inputPartnerRegisterEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.inputPartnerRegisterPassword.setError("Password is required");
                binding.inputPartnerRegisterPassword.requestFocus();
                return;
            }

            if (!RegexValidator.isValidPassword(password)) {
                binding.inputPartnerRegisterPassword.setError("The password must contain at least 8 characters, lowercase letter, uppercase letter, digit, and special character");
                binding.inputPartnerRegisterPassword.requestFocus();
                return;
            }

            if (reTypePassword.isEmpty()) {
                binding.inputPartnerRegisterReTypePassword.setError("Retype password is required");
                binding.inputPartnerRegisterReTypePassword.requestFocus();
                return;
            }

            if (!reTypePassword.equals(password)) {
                binding.inputPartnerRegisterReTypePassword.setError("Retype password does not match with password");
                binding.inputPartnerRegisterReTypePassword.requestFocus();
                return;
            }

            firebaseFirestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!ds.isEmpty()) {
                            binding.inputPartnerRegisterEmail.setError("This Email Already Registered. Try Another Email");
                            binding.inputPartnerRegisterEmail.requestFocus();
                        } else {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String uid = task.getResult().getUser().getUid();
                                                User user = User.builder()
                                                        .uid(uid)
                                                        .name(name)
                                                        .email(email)
                                                        .build();
                                                firebaseFirestore.collection("partners")
                                                        .document(uid)
                                                        .set(user)
                                                        .addOnSuccessListener(aVoid -> {
                                                            new Sweetalert(PartnerRegisterActivity.this, Sweetalert.SUCCESS_TYPE)
                                                                    .setTitleText("Success")
                                                                    .setContentText("Partner registered successfully")
                                                                    .show();

                                                            Intent intent = new Intent(PartnerRegisterActivity.this, PartnerMainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(PartnerRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                    binding.inputPartnerRegisterEmail.setError("This Email Already Registered. Try Another Email");
                                                    binding.inputPartnerRegisterEmail.requestFocus();
                                                } else {
                                                    Log.e("AuthError", task.getException().getMessage());
                                                }
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("PartnerRegisterActivity", "Error getting users", e);
                    });


        });
    }
}