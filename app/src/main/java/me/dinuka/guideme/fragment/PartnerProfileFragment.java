package me.dinuka.guideme.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.FragmentPartnerProfileBinding;
import me.dinuka.guideme.databinding.FragmentProfileBinding;

public class PartnerProfileFragment extends Fragment {

    private FragmentPartnerProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPartnerProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

            Log.i("ProfileFragment", "Log out Success");

            alertDialog.dismiss();
        });

        alertDialog.show();
    }
}