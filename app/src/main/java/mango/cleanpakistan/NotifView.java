package mango.cleanpakistan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class NotifView extends ActionBarActivity {
    ImageView ivPostedImage;
    TextView tvProposedMsg;
    EditText etNewDate;
    Button btnNo;
    Button btnYes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_view);
        ivPostedImage = (ImageView) findViewById(R.id.ivPostedImage);
        tvProposedMsg = (TextView) findViewById(R.id.proMsg);
        etNewDate = (EditText) findViewById(R.id.etDate);
        btnNo = (Button) findViewById(R.id.btnNo);
        btnYes = (Button) findViewById(R.id.btn_yes);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String objectId = intent.getStringExtra("objectId");
        Toast.makeText(this, "The object ID is " + objectId, Toast.LENGTH_SHORT).show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Form");
        query.whereEqualTo("objectId", objectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseFile file = parseObject.getParseFile("picture");
                    Uri fileUri = Uri.parse(file.getUrl());
                    Toast.makeText(NotifView.this, fileUri.toString(), Toast.LENGTH_SHORT).show();

                    Picasso.with(NotifView.this).load(fileUri.toString()).error(R.drawable.error_logo).placeholder(R.drawable.loading).into(ivPostedImage);

                    //Set Message
                    String mProposedMsg = parseObject.getString("message");
                    tvProposedMsg.setText(mProposedMsg);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notif_view, menu);
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
