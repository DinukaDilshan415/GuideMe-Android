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
import me.dinuka.guideme.adapter.ThingsToDoListingAdapter;
import me.dinuka.guideme.api.ThingsToDoApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentThingsToDoBinding;
import me.dinuka.guideme.model.ThingsToDo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ThingsToDoFragment extends Fragment {

    private FragmentThingsToDoBinding binding;
    List<ThingsToDo> thingstodo = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentThingsToDoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = binding.loadingImage;

        Glide.with(this)
                .load(R.drawable.loadingbuffering)
                .into(imageView);

        loadMainThingsToDo("key");

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadMainThingsToDo(String key) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        ThingsToDoApi thingsToDoApi = retrofit.create(ThingsToDoApi.class);

        Call<List<HashMap<String, Object>>> listingCall = thingsToDoApi.getMainThingsToDo(key);
        listingCall.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HashMap<String, Object>> responseBody = response.body();
                    List<Task<Uri>> tasks = new ArrayList<>();

                    for (HashMap<String, Object> thingtodo : responseBody) {
                        List<String> images = (List<String>) thingtodo.get("imageUris");
                        String imageUrl = images.get(0);
                        String fullPath = "things-to-do-images/" + imageUrl;

                        tasks.add(FirebaseStorage.getInstance().getReference(fullPath).getDownloadUrl());
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {

                        thingstodo.clear();

                        for (int i = 0; i < responseBody.size(); i++) {
                            HashMap<String, Object> thingsToDoMap = responseBody.get(i);
                            Task<Uri> currentTask = tasks.get(i);

                            String finalImageUrl = "";
                            if (currentTask.isSuccessful()) {
                                finalImageUrl = currentTask.getResult().toString();
                            } else {
                                finalImageUrl = "default_placeholder_url";
                                Log.e("Firebase", "Failed to get URL : " + thingsToDoMap.get("title"));
                            }

                            thingstodo.add(new ThingsToDo(
                                    ((Number) thingsToDoMap.get("id")).intValue(),
                                    thingsToDoMap.get("title").toString(),
                                    Double.parseDouble(thingsToDoMap.get("price").toString()),
                                    thingsToDoMap.get("location_name").toString(),
                                    Double.parseDouble(thingsToDoMap.get("rating").toString()),
                                    ((Number) thingsToDoMap.get("reviewCount")).intValue(),
                                    new ArrayList<>(Arrays.asList(finalImageUrl)),
                                    thingsToDoMap
                            ));
                        }
                        setupRecyclerView();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e("ThingsToDoFragment", "Error fetching :", t);
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.forYouSection.thingsToDoSectionContainer.setLayoutManager(layoutManager);

        ThingsToDoListingAdapter adapter = new ThingsToDoListingAdapter(thingstodo, selectedThingToDo -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedThingToDo", selectedThingToDo);

            ThingsToDoDetailsFragment fragment = new ThingsToDoDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.forYouSection.thingsToDoSectionTitle.setText("For You");
        binding.forYouSection.thingsToDoSectionContainer.setAdapter(adapter);

        binding.loadingLayout.setVisibility(View.GONE);
        binding.recyclerViewLayout.setVisibility(View.VISIBLE);
    }


}