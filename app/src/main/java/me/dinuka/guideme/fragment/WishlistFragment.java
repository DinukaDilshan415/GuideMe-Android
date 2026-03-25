package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.WishListListingAdapter;
import me.dinuka.guideme.databinding.FragmentWishlistBinding;
import me.dinuka.guideme.helper.WishlistHelper;
import me.dinuka.guideme.model.Hotel;
import me.dinuka.guideme.model.WishlistItem;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private List<WishlistItem> wishlistItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<HashMap<String, Object>> wishlist = WishlistHelper.getWishlist(getContext());

        if (wishlist.isEmpty()) {
            binding.loadingLayout.setVisibility(View.GONE);
            binding.noContentLayout.setVisibility(View.VISIBLE);
        } else {

            wishlistItems.clear();

            final int totalItems = wishlist.size();
            final int[] processedCount = {0};

            for (HashMap<String, Object> item : wishlist) {
                String imageFolder = "";
                int categoryId = ((Number) item.get("categoryId")).intValue();
                if (categoryId == 1) imageFolder = "hotel-images";
                else if (categoryId == 2) imageFolder = "things-to-do-images";
                else if (categoryId == 3) imageFolder = "service-images";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                List<String> images = (List<String>) item.get("imageUris");
                String imageUrl = images.get(0);
                String fullPath = imageFolder + "/" + imageUrl;

                storage.getReference(fullPath).getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            wishlistItems.add(new WishlistItem(
                                    ((Number) item.get("id")).intValue(),
                                    item.get("title").toString(),
                                    Double.parseDouble(item.get("price").toString()),
                                    item.get("location_name").toString(),
                                    Double.parseDouble(item.get("rating").toString()),
                                    ((Number) item.get("reviewCount")).intValue(),
                                    uri.toString(),
                                    item
                            ));
                            processedCount[0]++;

                            if (processedCount[0] == totalItems) {
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                binding.mywishlistRecyclerView.setLayoutManager(layoutManager);

                                WishListListingAdapter adapter = new WishListListingAdapter(wishlistItems, wishListItem -> {
                                    int selectedItemCategoryId = ((Number) wishListItem.get("categoryId")).intValue();
                                    Bundle bundle = new Bundle();

                                    if (selectedItemCategoryId == 1) {
                                        bundle.putSerializable("selectedHotel", wishListItem);
                                        HotelDetailsFragment fragment = new HotelDetailsFragment();
                                        fragment.setArguments(bundle);

                                        requireActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, fragment)
                                                .addToBackStack(null)
                                                .commit();

                                    } else if (selectedItemCategoryId == 2) {
                                        bundle.putSerializable("selectedThingToDo", wishListItem);
                                        ThingsToDoDetailsFragment fragment = new ThingsToDoDetailsFragment();
                                        fragment.setArguments(bundle);

                                        requireActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, fragment)
                                                .addToBackStack(null)
                                                .commit();

                                    } else if (selectedItemCategoryId == 3) {
                                        bundle.putSerializable("selectedService", wishListItem);
                                        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
                                        fragment.setArguments(bundle);

                                        requireActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, fragment)
                                                .addToBackStack(null)
                                                .commit();

                                    }
                                });

                                adapter.setOnDeleteListener(() -> {
                                    checkEmptyState();
                                });

                                binding.mywishlistRecyclerView.setAdapter(adapter);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Error", "Image failed", e);
                        });
            }

            binding.loadingLayout.setVisibility(View.GONE);
            binding.contentLayout.setVisibility(View.VISIBLE);
        }


        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void checkEmptyState() {
        if (wishlistItems == null || wishlistItems.isEmpty()) {
            binding.contentLayout.setVisibility(View.GONE);
            binding.noContentLayout.setVisibility(View.VISIBLE);
        } else {
            binding.contentLayout.setVisibility(View.VISIBLE);
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }
}