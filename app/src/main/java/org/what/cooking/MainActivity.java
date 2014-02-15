package org.what.cooking;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.fragment = new PlaceholderFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, this.fragment)
                    .commit();
        }
    }


    public void myClickHandler(View Target) {
        dispatchTakePictureIntent();
    }


    static final int REQUEST_TAKE_PHOTO = 1;

    public File getPhotoFile() {
        return photoFile;
    }

    private File photoFile = null;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("Image", "Capturing");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // ...
                Log.d("Image", "There was an rerooo");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void displayPhoto() {

        Fragment fragment = new FullSizePicture(this);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
//        ((ImageView) findViewById(R.id.fullsizeImage)).setImageBitmap(myBitmap);
//        Bitmap myBitmap = BitmapFactory.decodeFile(this.getPhotoFile().getAbsolutePath());
//        this.fragment.on
//                ((ImageView) this.fragment.getView().findViewById(R.id.fullsizeImage)).setImageBitmap(myBitmap);

    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("images");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public boolean uploadImageToServer(File image) {
        try {
            // think about scaling down the image
            String bnd = "*******";

            URL url = new URL("http://test.com");
            FileInputStream fis = new FileInputStream(image);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + bnd);

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes("--" + bnd + "\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"uplfile\"\n");
            dos.writeBytes("\n");

            int bytesAvail = fis.available();
            int maxBufSize = 1024 * 1024;
            int bfs = Math.min(bytesAvail, maxBufSize);
            byte[] buffer = new byte[bfs];

            int bytesRead = fis.read(buffer, 0, bfs);

            while(bytesRead > 0) {
                dos.write(buffer, 0, bfs);
                bytesAvail = fis.available();
                bfs = Math.min(bytesAvail, maxBufSize);
                bytesRead = fis.read(buffer, 0, bfs);
            }

            dos.writeBytes("\n");
            dos.writeBytes("--" + bnd + "--" + "\n");

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fis.close();
            dos.flush();
            dos.close();
        } catch (Exception ex) {
            // handle exception
            Log.e("Error", ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_TAKE_PHOTO:
                if(resultCode==RESULT_OK) {
                    displayPhoto();
                }
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public static  class FullSizePicture extends Fragment {
        private MainActivity activity;
        public FullSizePicture(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_fullsize_image, container, false);

            Bitmap myBitmap = BitmapFactory.decodeFile(activity.getPhotoFile().getAbsolutePath());
//            ((ImageView)rootView.findViewById(R.id.fullsizeImage)).setImageBitmap(myBitmap);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstance) {
            Bitmap myBitmap = BitmapFactory.decodeFile(activity.getPhotoFile().getAbsolutePath());
            ((ImageView)view.findViewById(R.id.fullsizeImage)).setImageBitmap(myBitmap);
            Log.d("Created View", "Image View Created");
        }
    }

}
