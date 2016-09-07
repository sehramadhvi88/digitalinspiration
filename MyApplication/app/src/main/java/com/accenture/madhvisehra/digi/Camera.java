package com.accenture.madhvisehra.digi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by dell on 14-08-2016.
 */
public class Camera  extends Activity implements View.OnClickListener{

    Button ib;
    Button b,loc;

    Intent i;
    final static int cameraData=0;
    private Bitmap bmp;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private static final int PERMISSION_REQUEST_CODE=1;
    View view;
    private float latitude;
    private float longitude;

    /* Photo album for this application */
    private String getAlbumName() {
        return "CameraSample";
    }

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
        initializeVar();

        ib.setOnClickListener(this);
        b.setOnClickListener(this);

    }

    private void initializeVar(){
        ib=(Button)findViewById(R.id.ibTakePic);
        b=(Button)findViewById(R.id.bSelect);
        loc=(Button)findViewById(R.id.bGps);
    }


    @Override
    public void onClick(View v) {

        view=v;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
          mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        switch(v.getId())
        {
            case R.id.ibTakePic:
                if(checkPermission()) {
                    if (isLocationEnabled()) {
                        i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = null;

                        try {
                            f = setUpPhotoFile();
                            mCurrentPhotoPath = f.getAbsolutePath();
                            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        } catch (IOException e) {
                            e.printStackTrace();
                            f = null;
                            mCurrentPhotoPath = null;
                        }

                        startActivityForResult(i, cameraData);
                    } else {
                        showAlert();
                    }
                }else{
                    requestPermission();
                }
                break;
            case R.id.bSelect:
                try {
                    getApplicationContext().setWallpaper(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bGps:
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                break;
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
      // Bundle extras=data.getExtras();
       // bmp=(Bitmap)extras.get("data");


        //    iv.setImageBitmap(bmp);
        /*    ByteArrayOutputStream bytes=new ByteArrayOutputStream();
           //bmp.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".jpg");
            FileOutputStream fo;*/


            if (mCurrentPhotoPath != null) {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Log.i("mCurrentPhotoPath",mCurrentPhotoPath);
                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getApplicationContext().sendBroadcast(mediaScanIntent);
                //mCurrentPhotoPath = null;

                ReadExif(f.toString());
                Intent cv=new Intent(Camera.this,Cameraview.class);
                Bundle basket=new Bundle();
                i.getExtras();
                basket.putString("imagePath",contentUri.toString());
                basket.putString("mCurrentPhotoPath",mCurrentPhotoPath);
                cv.putExtras(basket);
                startActivity(cv);

            }
//
//            try {
//                destination.createNewFile();
//                fo = new FileOutputStream(destination);
//                fo.write(bytes.toByteArray());
//                fo.close();
//            }catch(FileNotFoundException foe){
//                foe.printStackTrace();
//            }catch(IOException ioe){
//                ioe.printStackTrace();
//            }

            //  Exif.setText(ReadExif(destination.toString()));



        }
    }


    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ;
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    // need to check network on ?

   private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
       int result3= ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
       int result2= ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && writeAccepted && readAccepted)
                        Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    else {

                        Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    void ReadExif(String file){
        String exif="Exif: " + file;
        try {
            ExifInterface exifInterface = new ExifInterface(file);

            exif += "\nIMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            exif += "\nIMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            exif += "\nGPS related:";

            float[] LatLong = new float[2];
            if(exifInterface.getLatLong(LatLong)){
                latitude=LatLong[0];
                longitude=LatLong[1];
                exif += "\n latitude= " + LatLong[0];
                exif += "\n longitude= " + LatLong[1];
            }else{
                exif += "Exif tags are not available!";
            }

            exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

            Toast.makeText(this,
                    "finished",
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        Log.i("exif=",exif);
    }

}
