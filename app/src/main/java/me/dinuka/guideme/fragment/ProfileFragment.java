package me.dinuka.guideme.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executors;

import me.dinuka.guideme.R;
import me.dinuka.guideme.activity.LoginActivity;
import me.dinuka.guideme.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        binding.editBtn1.setOnClickListener(v -> {
            EditProfileSheet editProfileSheet = new EditProfileSheet();

            editProfileSheet.show(getParentFragmentManager(), "EditProfileTag");
        });

        binding.logoutBtn.setOnClickListener(v -> {
            openDialog();
        });

        getParentFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            String imageUriString = bundle.getString("bundleKey");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                binding.profileImage.setImageURI(imageUri);
            }
        });
    }

    private void openDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> alertDialog.dismiss());
        dialogView.findViewById(R.id.btnLogOut).setOnClickListener(v -> {

            logout();

            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void logout(){
        mAuth.signOut();

        CredentialManager credentialManager = CredentialManager.create(getContext());
        // When a user signs out, clear the current user credential state from all credential providers.
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
//                        updateUI(null);
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e("ProfileFragment", "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                });

        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}