package com.app.multipurposetranslator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class ThirdFragment extends Fragment {


    RecyclerView recview;
    String url = "https://language-translator-5ad9b-default-rtdb.firebaseio.com/";
    FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_third, container, false);

        recview = v.findViewById(R.id.recview);
        recview.setLayoutManager((new LinearLayoutManager(getContext())));


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(FirebaseDatabase.getInstance(url).getReference().child("Words"), Model.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Model, FieldsViewHolder>(options) {

            @NonNull
            @Override
            public FieldsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
                return new FieldsViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull FieldsViewHolder holder, int position, @NonNull Model model) {
                holder.setSavedWords(model.getWords());
            }
        };
        adapter.startListening();
        recview.setAdapter(adapter);
        recview.setHasFixedSize(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }



}