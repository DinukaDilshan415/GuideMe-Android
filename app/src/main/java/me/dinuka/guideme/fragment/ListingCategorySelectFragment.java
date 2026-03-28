package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.CategoryChooseAdapter;
import me.dinuka.guideme.adapter.MenuAdapter;
import me.dinuka.guideme.api.FacilityApi;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentListingCategorySelectBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.ListingTypeDTO;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.MenuCategory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;


public class ListingCategorySelectFragment extends Fragment {

    private FragmentListingCategorySelectBinding binding;
    private MenuCategory selectedCategory;
    private List<ListingTypeDTO> listingTypeDTOS;
    List<MenuCategory> categories;

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

        categories = new ArrayList<>();

        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        loadCategories();

        binding.nextBtn.setOnClickListener(v -> {
            if (selectedCategory != null) {
                Bundle bundle = new Bundle();
                bundle.putString("categoryName",selectedCategory.getTitle());
                bundle.putInt("categoryId",selectedCategory.getId());

                    CommanListingDetailsFragment fragment = new CommanListingDetailsFragment();
                    fragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.partner_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();

            }else {
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Select Category")
                        .show();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void loadCategories() {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        ListingApi listingApi = retrofit.create(ListingApi.class);

        Call<List<ListingTypeDTO>> listCall = listingApi.getAllListingTypes();
        listCall.enqueue(new Callback<List<ListingTypeDTO>>() {
            @Override
            public void onResponse(Call<List<ListingTypeDTO>> call, Response<List<ListingTypeDTO>> response) {
                if (response.isSuccessful()) {
                     listingTypeDTOS = response.body();
                    for (ListingTypeDTO f : listingTypeDTOS) {

                        String resourceName = f.getIcon();
                        String entryName = resourceName.substring(resourceName.lastIndexOf('.') + 1);

                        int resId = getResources().getIdentifier(entryName, "drawable", getContext().getPackageName());

                        if (resId != 0) {
                            categories.add(new MenuCategory(f.getId(), f.getType(), resId));
                        }
                    }

//                    categories.add(new MenuCategory(0, "Hotels", R.drawable.hotel_svgrepo_com));
//                    categories.add(new MenuCategory(1, "Things To Do", R.drawable.things_to_do));
//                    categories.add(new MenuCategory(2, "Services", R.drawable.service_svgrepo_com));

                    RecyclerView recyclerView = binding.categoryChooseRecyclerView;
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                    CategoryChooseAdapter adapter = new CategoryChooseAdapter(categories, (item, position) -> {
                        selectedCategory = new MenuCategory();
                        selectedCategory.setId(item.getId());
                        selectedCategory.setTitle(item.getTitle());
                        selectedCategory.setIconRes(item.getIconRes());
                    });

                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("ListingCategorySelectFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<List<ListingTypeDTO>> call, Throwable t) {
                Log.e("ListingCategorySelectFragment", "Error fetching ", t);
            }
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