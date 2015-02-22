package mango.cleanpakistan;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity {
    Button btnShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            ParseAnalytics.trackAppOpened(getIntent());
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                // do stuff with the user
                Log.i(this.getLocalClassName(), currentUser.toString());
            } else {
                // show the signup or login screen
                navigateToLogin();
            }


        btnShot = (Button) findViewById(R.id.btnShot);
        btnShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Form.class);
                startActivity(i);
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToLogin();
        }
        return super.onOptionsItemSelected(item);
    }

}