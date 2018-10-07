package il.co.gabel.android.uhcarmel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import il.co.gabel.android.uhcarmel.firebase.objects.redalert.RedAlertMessage;

public class UploadMessageAsyncTask extends AsyncTask<String,Void,List<Uri>> {
    private static final String TAG = UploadMessageAsyncTask.class.getSimpleName();
    private Context context;
    private List<Uri> images;
    private RedAlertMessage message;
    private FusedLocationProviderClient fusedLocationClient;
    private  Location mLastLocation;
    private UploadListener listener;
    private StorageReference ref;
    private UploadTask uploadTask;

    public interface UploadListener{
        void uploadCompleted(RedAlertMessage message);
    }



    public UploadMessageAsyncTask(List<Uri> images, Context context, RedAlertMessage message, UploadListener listener) {
        this.context=context;
        this.images=images;
        this.message=message;
        this.listener=listener;

    }

    @Override
    protected void onPostExecute(List<Uri> uris) {
        super.onPostExecute(uris);
        message.setImages(uris);
        message.setLocation(mLastLocation);
        listener.uploadCompleted(message);

    }

    @SuppressLint("MissingPermission")
    @Override
    protected List<Uri> doInBackground(String... strings) {
        final List<Uri> messageImages = new ArrayList<>();
        for(final Uri imageUri : images) {

            ref = FirebaseStorage.getInstance().getReference(context.getString(R.string.red_alert_folder)+UUID.randomUUID().toString());
            uploadTask = ref.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            });
            Task x = urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "onComplete: url for file: "+downloadUri.toString() );
                        messageImages.add(downloadUri);

                    } else {
                        Log.e(TAG, "onComplete: unable to upload file: "+imageUri.toString() );
                    }
                }
            });
            try {
                Log.e(TAG, "doInBackground: before" );
                Tasks.await(x);
                x=null;
                urlTask=null;
                uploadTask = null;
                Log.e(TAG, "doInBackground: after" );
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            Task<Location> locationTask =  fusedLocationClient.getLastLocation();
            Task<Location> locationCompleteTask = locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastLocation = task.getResult();
                    } else {
                        mLastLocation = null;
                    }
                }
            });
            try {
                Tasks.await(locationCompleteTask);
                locationCompleteTask=null;
                locationTask=null;
                fusedLocationClient=null;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }

        return messageImages;
    }
}
