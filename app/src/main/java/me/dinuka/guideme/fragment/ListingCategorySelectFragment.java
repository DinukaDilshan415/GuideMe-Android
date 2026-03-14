package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.CategoryChooseAdapter;
import me.dinuka.guideme.adapter.MenuAdapter;
import me.dinuka.guideme.databinding.FragmentListingCategorySelectBinding;
import me.dinuka.guideme.model.MenuCategory;


public class ListingCategorySelectFragment extends Fragment {

    private FragmentListingCategorySelectBinding binding;
    private String categoryName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListingCategorySelectBinding.inflate(inflater, container, false);
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

        List<MenuCategory> categories = new ArrayList<>();
        categories.add(new MenuCategory(0, "Hotels", R.drawable.hotel_svgrepo_com));
        categories.add(new MenuCategory(1, "Things To Do", R.drawable.things_to_do));
        categories.add(new MenuCategory(2, "Services", R.drawable.service_svgrepo_com));

        RecyclerView recyclerView = binding.categoryChooseRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        CategoryChooseAdapter adapter = new CategoryChooseAdapter(categories, (item, position) -> {
                categoryName = item.getTitle();
        });

        recyclerView.setAdapter(adapter);

        binding.nextBtn.setOnClickListener(v -> {
            if (categoryName != null && !categoryName.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("categoryName",categoryName);

                CommanListingDetailsFragment fragment = new CommanListingDetailsFragment();
                fragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
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