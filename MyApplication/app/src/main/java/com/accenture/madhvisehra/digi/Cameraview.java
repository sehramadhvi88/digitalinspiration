package com.accenture.madhvisehra.digi;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by dell on 21-08-2016.
 */
public class Cameraview extends FragmentActivity implements OnMapReadyCallback {

    ImageView iv;
    TextView Exif;
    SupportMapFragment smf;
    float latitude,longitude;
    GoogleMap mMap;
    MarkerOptions options;
    LatLng position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoview);
        initializeVar();

        Bundle bskt=getIntent().getExtras();
        String imagePath=bskt.getString("imagePath");
        String mCurrentPhotoPath=bskt.getString("mCurrentPhotoPath");
        String filePath=imagePath.replace("file://","");
        Log.i("filePath=", filePath);
        File imgFile = new File(filePath);
        Log.i("imagePath=", imagePath);
        Log.i("imgFile=", imgFile.getAbsolutePath());
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


        Bitmap d = new BitmapDrawable(getApplicationContext().getResources() , imgFile.getAbsolutePath()).getBitmap();
        int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bm, 512, nh, true);
        iv.setImageBitmap(scaled);
        //iv.setImageBitmap(bm);
        //iv.setImageURI(Uri.parse(imagePath));

      Exif.setText(ReadExif(imgFile.getAbsolutePath()));


        position = new LatLng(latitude, longitude);
        // Instantiating MarkerOptions class
        options = new MarkerOptions();
        // Setting position for the MarkerOptions
        options.position(position);
        options.title(getAddressFromLatLng( position ) );


        // Getting reference to google map
        smf.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(options);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(position, 5);
        mMap.animateCamera(yourLocation);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void initializeVar(){
        iv=(ImageView)findViewById(R.id.ivReturnedPic);
        Exif = (TextView)findViewById(R.id.exif);
         smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }


    String ReadExif(String file){
        String exif="Exif: " + file;
        try {
            Log.i("in read exif=",file);
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
        return exif;
    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder(this);

        String address = "";
        try {
            address = geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        Toast.makeText(this,
                address,
                Toast.LENGTH_LONG).show();

        return address;
    }

}
