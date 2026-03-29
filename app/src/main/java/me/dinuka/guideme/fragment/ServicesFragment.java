package me.dinuka.guideme.fragment;

import android.net.Uri;
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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.ServiceListingAdapter;
import me.dinuka.guideme.adapter.ThingsToDoListingAdapter;
import me.dinuka.guideme.api.ServiceApi;
import me.dinuka.guideme.api.ThingsToDoApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentServicesBinding;
import me.dinuka.guideme.model.Service;
import me.dinuka.guideme.model.ThingsToDo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServicesFragment extends Fragment {

    private FragmentServicesBinding binding;
    List<Service> services = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = binding.loadingImage;

        Glide.with(this)
                .load(R.drawable.loadingbuffering)
                .into(imageView);

        loadMainServices("key");

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadMainServices(String key) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        ServiceApi serviceApi = retrofit.create(ServiceApi.class);

        Call<List<HashMap<String, Object>>> listingCall = serviceApi.getMainServices(key);
        listingCall.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HashMap<String, Object>> responseBody = response.body();
                    List<Task<Uri>> tasks = new ArrayList<>();

                    for (HashMap<String, Object> service : responseBody) {
                        List<String> images = (List<String>) service.get("imageUris");
                        String imageUrl = images.get(0);
                        String fullPath = "service-images/" + imageUrl;

                        tasks.add(FirebaseStorage.getInstance().getReference(fullPath).getDownloadUrl());
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {

                        services.clear();

                        for (int i = 0; i < responseBody.size(); i++) {
                            HashMap<String, Object> serviceMap = responseBody.get(i);
                            Task<Uri> currentTask = tasks.get(i);

                            String finalImageUrl = "";
                            if (currentTask.isSuccessful()) {
                                finalImageUrl = currentTask.getResult().toString();
                            } else {
                                finalImageUrl = "default_placeholder_url";
                                Log.e("Firebase", "Failed to get URL : " + serviceMap.get("title"));
                            }

                            services.add(new Service(
                                    ((Number) serviceMap.get("id")).intValue(),
                                    serviceMap.get("title").toString(),
                                    Double.parseDouble(serviceMap.get("price").toString()),
                                    serviceMap.get("location_name").toString(),
                                    Double.parseDouble(serviceMap.get("rating").toString()),
                                    ((Number) serviceMap.get("reviewCount")).intValue(),
                                    new ArrayList<>(Arrays.asList(finalImageUrl)),
                                    serviceMap
                            ));
                        }
                        setupRecyclerView();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e("ServicesFragment", "Error fetching :", t);
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.forYouSection.serviceSectionContainer.setLayoutManager(layoutManager);

        ServiceListingAdapter adapter = new ServiceListingAdapter(services, selectedService -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedService", selectedService);

            ServiceDetailsFragment fragment = new ServiceDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.forYouSection.serviceSectionTitle.setText("For You");
        binding.forYouSection.serviceSectionContainer.setAdapter(adapter);

        binding.loadingLayout.setVisibility(View.GONE);
        binding.recyclerViewLayout.setVisibility(View.VISIBLE);
    }

}