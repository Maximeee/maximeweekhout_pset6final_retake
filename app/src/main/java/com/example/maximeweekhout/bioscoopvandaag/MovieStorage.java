package com.example.maximeweekhout.bioscoopvandaag;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Maxime Weekhout on 25-9-2016.
 */

// https://developer.android.com/guide/topics/data/data-storage.html
public class MovieStorage {

    List<String> localStorage = new ArrayList();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    public MovieStorage(Context context) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference = database.getReference(user.getUid());
        } else {
            databaseReference = database.getReference("default");
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                System.out.println("Change detected!");

                if (dataSnapshot.getValue() != null) {
                    localStorage = (List) dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("ERROR:" + error);
            }
        });
    }

    public void add(StorableShow show) {
        System.out.println(localStorage);
        localStorage.add(show.getJson());
        databaseReference.setValue(localStorage);
    }

    /**
     * Removes items from list
     * @param show
     */
    public void remove(StorableShow show) {

        for (String item: localStorage) {
            try {
                StorableShow showItem = new StorableShow(item);

                if (showItem.getTitle().equals(show.getTitle()) &&
                        showItem.getShow().getStart().equals(show.getShow().getStart())) {
                    localStorage.remove(item);
                    break;
                }
            } catch (Exception e) {

            }
        }

        databaseReference.setValue(localStorage);
    }
}