package me.dinuka.guideme.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityRegisterBinding;
import me.dinuka.guideme.model.User;
import me.dinuka.guideme.util.RegexValidator;
import taimoor.sultani.sweetalert2.Sweetalert;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gotoLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.registerBtn.setOnClickListener(v -> {
            String name = binding.inputRegisterName.getText().toString().trim();
            String email = binding.inputRegisterEmail.getText().toString().trim();
            String password = binding.inputRegisterPassword.getText().toString().trim();
            String reTypePassword = binding.inputRegisterReTypePassword.getText().toString().trim();

            if(name.isEmpty()){
                binding.inputRegisterName.setError("Name is required");
                binding.inputRegisterName.requestFocus();
                return;
            }

            if(email.isEmpty()){
                binding.inputRegisterEmail.setError("Email is required");
                binding.inputRegisterEmail.requestFocus();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.inputRegisterEmail.setError("Please provide a valid email");
                binding.inputRegisterEmail.requestFocus();
                return;
            }

            if(password.isEmpty()){
                binding.inputRegisterPassword.setError("Password is required");
                binding.inputRegisterPassword.requestFocus();
                return;
            }

            if(!RegexValidator.isValidPassword(password)){
                binding.inputRegisterPassword.setError("The password must contain at least 8 characters, lowercase letter, uppercase letter, digit, and special character");
                binding.inputRegisterPassword.requestFocus();
                return;
            }

            if(reTypePassword.isEmpty()){
                binding.inputRegisterReTypePassword.setError("Retype password is required");
                binding.inputRegisterReTypePassword.requestFocus();
                return;
            }

            if(!reTypePassword.equals(password)){
                binding.inputRegisterReTypePassword.setError("Retype password does not match with password");
                binding.inputRegisterReTypePassword.requestFocus();
                return;
            }

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
                                firebaseFirestore.collection("users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            new Sweetalert(RegisterActivity.this, Sweetalert.SUCCESS_TYPE)
                                                    .setTitleText("Success")
                                                    .setContentText("YUser registered successfully")
                                                    .show();

                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}