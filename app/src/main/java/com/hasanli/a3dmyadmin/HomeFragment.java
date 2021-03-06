package com.hasanli.a3dmyadmin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    View view;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    //view objects
    private TextView textViewUserEmail;
    private TextView textViewSayi;
    private Button buttonLogout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            getActivity().finish();
            //starting login activity
            startActivity(new Intent(getContext(), MainActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) view.findViewById(R.id.textViewUserEmail);
        textViewSayi = (TextView) view.findViewById(R.id.textViewProducts);
        buttonLogout = (Button) view.findViewById(R.id.buttonLogout);

        //product sayini tap
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("spinners");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long sayi = dataSnapshot.getChildrenCount();
                textViewSayi.setText("Mehsul sayi : "+sayi);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //displaying logged in user name
        textViewUserEmail.setText("Welcome "+user.getEmail());


        //adding listener to button
        buttonLogout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            getActivity().finish();
            //starting login activity
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }
}
