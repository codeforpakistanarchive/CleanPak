package mango.cleanpakistan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class Form extends ActionBarActivity {
    private static final int TAKE_PHOTO_REQUEST = 1001;
    private static final String TAG = mango.cleanpakistan.MainActivity.class.getSimpleName();
    private static final String FILE_NAME = "MyFile";
    Button btnReport;
    ImageView imgBtn;
    EditText etMessage;
    EditText etEventDate;
    Bitmap bitmap;
    private Uri imgURi;
    private Uri selectedImageUri;
    private Uri selectedImage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Parse.initialize(this, "QYP3WEGkoWbgGFcjUVO6n4x18s7pLziFbHJHZcDf", "yKm8tqxzFIkXnmWlY9jHISd6wPbTD9zcSS13ysdo");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgBtn = (ImageView) findViewById(R.id.imageButton);
        btnReport = (Button) findViewById(R.id.btn_report);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etEventDate = (EditText) findViewById(R.id.etEventDate);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
//                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST);


            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent inShare = new Intent(Intent.ACTION_SEND);
//                inShare.setType("Image/png");
//                inShare.putExtra(Intent.EXTRA_STREAM, imgURi);
//                startActivity(inShare);

                setProgressBarIndeterminateVisibility(true);
                ParseObject message = new ParseObject("Form");
                message.put("username", ParseUser.getCurrentUser().getUsername());
                message.put("location", "SEECS, NUST University");
                message.put("message", etMessage.getText().toString());
                ParseGeoPoint point = new ParseGeoPoint(33.6455663,72.9560122);
                message.put("gPoint", point);



                ////
                byte[] fileBytes = FileHelper.getByteArrayFromFile(Form.this, selectedImageUri);

                if (fileBytes == null) {
                    message = null;
                } else {

                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }
                String fileName = FileHelper.getFileName(Form.this, selectedImageUri, "image");
                ParseFile file = new ParseFile(fileName, fileBytes);
                message.put("picture", file);


                message.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //success!
                            Toast.makeText(Form.this, "Successfully Uploaded Images", Toast.LENGTH_LONG).show();
                            setProgressBarIndeterminateVisibility(false);

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

                ParsePush push = new ParsePush();
                push.setMessage(etMessage.getText().toString() + ". The place near I-8 in Pindi is too dirty - lets join and clean Pakistan on Sunday1st, March ");
                push.sendInBackground();

                ////
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG + "OnActivityResult Aizaz", Integer.toString(requestCode) + Integer.toString(resultCode));

//           imgURi = data.getData();
//
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgURi);
//                bitmap = (Bitmap) data.getExtras().get("data");
//                imgBtn.setImageBitmap(bitmap);
//            Toast.makeText(Form.this, "Image got the data: " + data.getData().toString(), Toast.LENGTH_SHORT).show();


        if (requestCode == TAKE_PHOTO_REQUEST) {


            if (resultCode == Activity.RESULT_OK) {

                try {
                    selectedImage = selectedImageUri;
                    //getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                    imgBtn.setImageBitmap(bitmap);

                    ////


                    ////

                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }

            } else {
                selectedImageUri = null;
                imgBtn.setImageBitmap(null);
            }

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
}
