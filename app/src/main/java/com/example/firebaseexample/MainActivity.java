package com.example.firebaseexample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // Member Variables
    EditText editTitle, editContent;
    Button btnPost, btnUpdate, btnDelete;
    RecyclerView recyclerView;
    // Firebase Variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Post> options;
    FirebaseRecyclerAdapter<Post, RecyclerViewHolder> adapter;

    Post selectedPost;
    String selectedKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Variables
        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
        btnPost = findViewById(R.id.btn_post);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        recyclerView = findViewById(R.id.recycler_view);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("FIREBASE");

        // RecyclerView LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // databaseReference ValueEventListener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Button onClickListeners
        // Post Button
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
                editTitle.setText("");
                editContent.setText("");
            }
        });
        // Update Button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference
                        .child(selectedKey)
                        .setValue(new Post(editTitle.getText().toString(), editContent.getText().toString()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                editTitle.setText("");
                editContent.setText("");
            }
        });
        // Delete Button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference
                        .child(selectedKey)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                editTitle.setText("");
                editContent.setText("");
            }
        });

        // Display comments
        displayComment();
    }

    // Overrides
    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    // Methods
    // Method to post comment to database
    private void postComment() {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();

        Post post = new Post(title, content);

        databaseReference.push() // Creates unique id for comment
                .setValue(post);

        adapter.notifyDataSetChanged();
    }
    // Method to display comment to database
    private void displayComment() {
        options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(databaseReference, Post.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<Post, RecyclerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position, @NonNull final Post model) {
                        holder.textTitle.setText(model.getTitle());
                        holder.textContent.setText(model.getContent());

                        // ItemClickListener for the update and delete
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                selectedPost = model;
                                selectedKey = getSnapshots().getSnapshot(position).getKey();
                                Log.d("KEY_ITEM", selectedKey+"");

                                // Bind data
                                editTitle.setText(model.getTitle());
                                editContent.setText(model.getContent());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.post_item,
                                parent, false);
                        return new RecyclerViewHolder(itemView);
                    }
                };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
