package mango.cleanpakistan;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Form extends ActionBarActivity implements LocationListener {
    private static final int TAKE_PHOTO_REQUEST = 01;
    private static final String TAG = mango.cleanpakistan.MainActivity.class.getSimpleName();
    private static final String FILE_NAME = "CleanPakistan";
    private static final int CHOOSE_FROM_GALLERY_REQUEST = 02;
    String curObjectId;
    JSONObject jsonObject;
    Button btnReport;
    ImageView imgBtn;
    EditText etMessage;
    EditText etEventDate;
    Bitmap bitmap;
    private Uri imgURi;
    private Uri selectedImageUri;
    private Uri selectedImage;

    /**
     * *location*****
     */
    double latitude;
    double longitude;
    String addressText = "";
    /**
     * *****Fin*******
     */

    //    private ProgressBar progressBar;

    public Location location;
    LocationManager locationManager;
    String provider;
    Geocoder geocoder;

    protected DialogInterface.OnClickListener mDialogInterface = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: //Take Photo
                    startCamera();
                    break;
                case 1: //Choose from Gallery
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, CHOOSE_FROM_GALLERY_REQUEST);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
//        Parse.initialize(this, "QYP3WEGkoWbgGFcjUVO6n4x18s7pLziFbHJHZcDf", "yKm8tqxzFIkXnmWlY9jHISd6wPbTD9zcSS13ysdo");
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize Views
        initViews();

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Form.this);
                builder.setTitle("Choose Action").setItems(R.array.camera_choices,
                        mDialogInterface);
                AlertDialog dialog = builder.create();
                dialog.show();
//                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST);


            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******Location*****/
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                geocoder = new Geocoder(Form.this);
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 400, 1, Form.this);
                location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    onLocationChanged(location);
                    setProgressBarIndeterminateVisibility(true);
                    ParseObject message = new ParseObject("Form");
                    message.put("username", ParseUser.getCurrentUser().getUsername());
                    message.put("location", addressText);
                    message.put("message", etMessage.getText().toString());
                    ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
                    message.put("gPoint", point);

                    ////
                    byte[] fileBytes = FileHelper.getByteArrayFromFile(Form.this, selectedImage);

                    if (fileBytes == null) {
                        message = null;
                    } else {
                        fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                    }
                    String fileName = FileHelper.getFileName(Form.this, selectedImage, "image");
                    ParseFile file = new ParseFile(fileName, fileBytes);
                    message.put("picture", file);


                    final ParseObject finalMessage = message;
                    message.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                //success!
                                Toast.makeText(Form.this, "Successfully Uploaded Images", Toast.LENGTH_LONG).show();
                                curObjectId = finalMessage.getObjectId();
                                try {
                                    jsonObject = new JSONObject();
                                    jsonObject.put("objectId", curObjectId);
                                    jsonObject.put("alert", etMessage.getText().toString());
                                    Toast.makeText(Form.this, jsonObject.toString(), Toast.LENGTH_LONG).show();

                                    ParsePush push = new ParsePush();
                                    push.setData(jsonObject);
                                    push.sendInBackground();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        Form.this);
                                builder.setMessage(e.getMessage().toString())
                                        .setTitle("Error Uploading")
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });

                } else {
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation((LocationManager.NETWORK_PROVIDER));
                    }
                    onLocationChanged(location);
                    if (location == null)
                        Toast.makeText(Form.this, "Please turn on GPS First" + location, Toast.LENGTH_SHORT).show();

                }
                ////
            }
        });

    }


    private void initViews() {
        imgBtn = (ImageView) findViewById(R.id.imageButton);
        btnReport = (Button) findViewById(R.id.btn_report);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etEventDate = (EditText) findViewById(R.id.etEventDate);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG + "OnActivityResult Aizaz", Integer.toString(requestCode) + Integer.toString(resultCode));
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST:
                    try {
                        selectedImage = selectedImageUri;
                        //getContentResolver().notifyChange(selectedImage, null);
                        ContentResolver cr = getContentResolver();
                        Bitmap bitmap;
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        imgBtn.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                    break;

                case CHOOSE_FROM_GALLERY_REQUEST:
                    try {
                        selectedImage = data.getData();
                        ContentResolver cr = getContentResolver();
                        Bitmap bitmap;
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        imgBtn.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                    break;
            }
        } else {
            selectedImageUri = null;
            imgBtn.setImageBitmap(null);
        }

    }


    public void startCamera() {
        File photo = null;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            photo = new File(android.os.Environment
                    .getExternalStorageDirectory(), FILE_NAME);
        } else {
            photo = new File(getCacheDir(), FILE_NAME);
        }
        if (photo != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            selectedImageUri = Uri.fromFile(photo);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();


        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            for (Address address : addresses) {
                //Toast.makeText(Form.this,"Address: "+address.getAddressLine(0),Toast.LENGTH_LONG).show();
                addressText = addressText + address.getAddressLine(0) + ", ";
                //Toast.makeText(Form.this,addressText,Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(Form.this, "" + e, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}




