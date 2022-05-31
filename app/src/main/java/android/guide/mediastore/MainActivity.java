package android.guide.mediastore;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int CODE;
    String TAG;
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = "msg";
        CODE =1;
//        TODO 1.2 GRANTED THE PERMISSION
        getReadPermission();
//  TODO 3 CREATE AN ARRAY LIST FROM THE CUSTOM CLASS I'VE MADE IN TODO2
        List<ImageStock> imageList = new ArrayList<ImageStock>();


//  assign images data
        imageList = myImagelist();
//        get one image from the list
        ImageStock imageStock = imageList.get(0);
//        print size of list = which is the number of images capture from external storage
        Log.d(TAG, "onCreate: IMAGE LIST SIZE = "+imageList.size());


//  fill image data into one string
        String description;
        description = "NAME: ";
        description+=("\n"+imageStock.getName());
        description+=("\n DIM : "+imageStock.getWidth()+"X"+imageStock.getHigh());
        description+=("\n SIZE : "+imageStock.getSize()+"KB");
        description+=("\n DATE : "+imageStock.getDate());
        description+=("\n TYPE : "+imageStock.getType());
        description+=("\n URI : "+imageStock.getUri());



        imageView = findViewById(R.id.imgView);
        textView = findViewById(R.id.descView);

//        TODO 5 : assign data to views
        textView.setText(description);
        imageView.setImageURI(imageStock.getUri());
    }




// TODO 4 THIS METHOD WILL SCAN ALL IMAGE DATA INSIDE YOUR EXTERNAL STORAGE
    public List<ImageStock> myImagelist(){
        List<ImageStock> imageList = new ArrayList<ImageStock>();
        Uri collection;
//        Is the current sdk is 29 or higher?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE
        };
//        String selection = MediaStore.Images.Media.MIME_TYPE +
//                " = ?";
//        String[] selectionArgs = new String[]{
//                "JPEG"
//        };
        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int widthColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
            int heightColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
            int dateColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int typeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int width = cursor.getInt(widthColumn);
                int height = cursor.getInt(heightColumn);
                String date = cursor.getString(dateColumn);
                String type = cursor.getString(typeColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                imageList.add(new ImageStock(contentUri, name, width, height, date, type, size));
            }
        }
        return imageList;
    }


// TODO 1.3 override this method
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "onCreate: " + "permission  accpeted");
                }
                break;

            default:
                break;
        }
    }

    public void getReadPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE);
        } else {
            Log.d(TAG, "onCreate: "+"permission  accpeted");
        }
    }
}