package com.example.firebaserdcrud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImg;
    Button saveBtn;
    EditText uploadTopic, uploadDesc, uploadLang;
    String imageUrl;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uploadImg=findViewById(R.id.uploadImage);
        uploadTopic=findViewById(R.id.uploadTopicEdt);
        uploadDesc=findViewById(R.id.uploadDescEdt);
        uploadLang=findViewById(R.id.uploadLangEdt);

        saveBtn=findViewById(R.id.saveBtn);
        // This launcher is responsible for launching the activity and receiving the result.
        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(//registerForActivityResult: This method registers an activity result callback for a given contract
                new ActivityResultContracts.StartActivityForResult(),//This is a contract that defines the operation to start an activity for a result. It's a part of the Activity Result API and handles the creation of an intent to start the activity and the reception of the result.
                new ActivityResultCallback<ActivityResult>() {//This is a callback interface that will be invoked when the result of the activity is available. It takes an ActivityResult object as a parameter, which contains information about the result, such as the result code and data.
                    /*This method is called when the activity result is available.
                     It checks if the result code is RESULT_OK, indicating that the operation was successful.
                     If it's RESULT_OK, it retrieves the data from the result intent and sets it to uri, which likely represents an image URI.
                     Then, it sets the retrieved image URI to an ImageView named uploadImg. If the result code is not RESULT_OK, it displays a toast indicating that no image was selected.*/
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()== Activity.RESULT_OK){
                            Intent data=result.getData();
                            uri=data.getData();
                            uploadImg.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        //listener for ImageView
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker=new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        //save button listener
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    /*Get Storage Reference: It creates a reference to the Firebase Storage location where the image will be stored. The reference is based on the URI of the image selected by the user.
Show Progress Dialog: It creates and displays a progress dialog to indicate that the upload process is in progress.
Upload Image: It uploads the image to Firebase Storage using the putFile() method of StorageReference. It listens for the success and failure events of the upload task.
addOnSuccessListener: If the upload is successful, it gets the download URL of the uploaded image using getDownloadUrl() method.
Since getDownloadUrl() returns a Task, it waits for the task to complete using a while loop.
addOnFailureListener: If the upload fails, it dismisses the progress dialog.
Save Image URL: Once the image is successfully uploaded, it retrieves the download URL and saves it to a variable (imageUrl).
 Then, it calls another method (uploadData()) to save additional data related to the image to a database.
Dismiss Progress Dialog: Regardless of whether the upload is successful or not, it dismisses the progress dialog to indicate that the process is completed.*/

    public void saveData(){
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder=new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog=builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage=uriTask.getResult();
                imageUrl=urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData(){
        /*This part retrieves the text entered by the user in
        three EditText fields (uploadTopic, uploadDesc, and uploadLang).
         It converts the input into strings using the toString() method.*/
        String title=uploadTopic.getText().toString();
        String desc=uploadDesc.getText().toString();
        String lang=uploadLang.getText().toString();

        /*DataClass object is created using the data retrieved from the
        EditText fields (title, desc, and lang), along with the imageUrl obtained from the image upload process.
        This object encapsulates the data to be uploaded to the Firebase Realtime Database.*/
        DataClass dataClass=new DataClass(title,desc,lang,imageUrl);

        String currentDate= DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        //This part stores the DataClass object in the Firebase Realtime Database under the "Fab Data" node, with the title as the key.
        FirebaseDatabase.getInstance().getReference("Fab Data").child(currentDate)
                //The setValue() method sets the value of the specified location to the provided DataClass object.
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    //addOnCompleteListener listens for the completion of the upload task.
                    // If the upload is successful, it executes the code inside the onComplete method.
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    //addOnFailureListener listens for any failures that may occur during the upload process.
                    // If there's an error, it executes the code inside the onFailure method.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        //Overall, this method takes the input data, creates a corresponding DataClass object,
        // and then uploads this object to the Firebase Realtime Database,
        // handling both success and failure cases appropriately.
    }
}