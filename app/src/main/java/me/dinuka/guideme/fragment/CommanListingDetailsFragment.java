package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.FragmentCommanListingDetailsBinding;


public class CommanListingDetailsFragment extends Fragment {

    private FragmentCommanListingDetailsBinding binding;

    private String categoryName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommanListingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.textv1.setText(categoryName);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.GONE);
    }

}