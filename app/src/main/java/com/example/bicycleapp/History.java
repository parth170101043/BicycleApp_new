package com.example.bicycleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ListView listView;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Cycle cycle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cycle=new Cycle();
        setContentView(R.layout.activity_history);
        listView=(ListView)findViewById(R.id.listview);

        database=FirebaseDatabase.getInstance();
        ref=database.getReference("Cycle");
        list= new ArrayList<>();
        adapter= new ArrayAdapter<String>(this,R.layout.listlayout,R.id.text,list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //listView.setAdapter(null);
                adapter.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    cycle=ds.getValue(Cycle.class);
                    list.add(cycle.getName().toString());
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
