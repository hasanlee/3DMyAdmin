package com.hasanli.a3dmyadmin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 234;

    View view;
    EditText mModelNo;
    EditText mModelName;
    EditText mModelPrice;
    EditText mImgUrl;
    Button mAddBtn;
    Button mChooseBtn;
    ImageView mImageView;
    DatabaseReference mDatabase;
    private Uri filePath;
    StorageReference sRef;

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
        //mImgUrl = (EditText) view.findViewById(R.id.editTextImgURL);
        mAddBtn = (Button) view.findViewById(R.id.buttonAdd);
        mChooseBtn = (Button) view.findViewById(R.id.buttonChoose);
        mImageView = (ImageView) view.findViewById(R.id.imageView);

        mAddBtn.setOnClickListener(this);
        mChooseBtn.setOnClickListener(this);

        sRef = FirebaseStorage.getInstance().getReference();
        return view;
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                mImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addProduct(){
        if(TextUtils.isEmpty(mModelNo.getText().toString().trim())){
            mModelNo.setError("Enter model number!");
        } else {
            if (TextUtils.isEmpty(mModelName.getText().toString().trim())){
                mModelName.setError("Enter model name!");
            } else {
                if (TextUtils.isEmpty(mModelPrice.getText().toString().trim())){
                    mModelPrice.setError("Enter model price!");
                } else {

                        uploadFile();

                }
            }
        }
    }

    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            final StorageReference riversRef = sRef.child("spinners/"+mModelName.getText().toString().trim()+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getContext().getApplicationContext(), "File Uploaded ", Toast.LENGTH_SHORT).show();
                            //add item
                            Uri downloadUri = taskSnapshot.getDownloadUrl();

                            mDatabase = FirebaseDatabase.getInstance().getReference("spinners");
                            final String modelNo = mModelNo.getText().toString().trim();
                            String modelName = mModelName.getText().toString().trim();
                            Integer modelPrice = Integer.parseInt(mModelPrice.getText().toString().trim());


                            //yeni spinner obyekti yarat
                            final SpinnerModel model = new SpinnerModel();

                            //melumatlare elave et
                            model.setName(modelName);
                            model.setImage(downloadUri.toString());
                            model.setPrice(modelPrice);

                            mDatabase.child(modelNo).setValue(model);
                            Toast.makeText(getContext(), "Product added.", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getContext().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == mChooseBtn) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == mAddBtn) {
            addProduct();
        }

    }
}
