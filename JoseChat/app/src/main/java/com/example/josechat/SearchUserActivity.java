package com.example.josechat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.josechat.Utils.FirebaseUtil;
import com.example.josechat.adapter.SearchUserRecyclerAdapter;
import com.example.josechat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput=findViewById(R.id.search_username_input);
        searchButton=findViewById(R.id.sear_user_button);
        backButton=findViewById(R.id.back_button);
        recyclerView=findViewById(R.id.search_user_recyclerview);

        searchInput.requestFocus();

        backButton.setOnClickListener(view -> {
            onBackPressed();
        });

        searchButton.setOnClickListener(view -> {
            String searchTerm= searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Usuario no valido");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

    }

    void setupSearchRecyclerView(String searchTerm){

        Query query= FirebaseUtil.allUserCollectionReference().whereGreaterThanOrEqualTo("userName", searchTerm);
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build();

        adapter=new SearchUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }
}