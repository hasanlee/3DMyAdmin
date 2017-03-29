package com.hasanli.a3dmyadmin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment {

    View view;
    EditText mModelNo;
    EditText mModelName;
    EditText mModelPrice;
    EditText mImgUrl;
    Button mAddBtn;
    DatabaseReference mDatabase;


    public AddItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_additem, container, false);

        mModelNo = (EditText) view.findViewById(R.id.editTextModelNo);
        mModelName = (EditText) view.findViewById(R.id.editTextName);
        mModelPrice = (EditText) view.findViewById(R.id.editTextPrice);
        mImgUrl = (EditText) view.findViewById(R.id.editTextImgURL);
        mAddBtn = (Button) view.findViewById(R.id.buttonAdd);

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance().getReference("spinners");
                String modelNo = mModelNo.getText().toString().trim();
                String modelName = mModelName.getText().toString().trim();
                Integer modelPrice = Integer.parseInt(mModelPrice.getText().toString().trim());
                String modelImg = mImgUrl.getText().toString().trim();

                //yeni spinner obyekti yarat
                SpinnerModel model = new SpinnerModel();

                //melumatlare elave et
                model.setName(modelName);
                model.setImage(modelImg);
                model.setPrice(modelPrice);

                mDatabase.child(modelNo).setValue(model);
            }
        });





        return view;
    }

}
