package mango.cleanpakistan;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends ActionBarActivity {

    String username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLayout();



        final EditText input_user = (EditText)findViewById(R.id.txt_user);
        final EditText input_pass = (EditText)findViewById(R.id.etpwdu);

        Button btn_login = (Button)findViewById(R.id.btn_lgn);

        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                username = input_user.getText().toString();
                password = input_pass.getText().toString();

                /////////////



                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)) {

                    input_user.setError("please fill the empty fields first");
                    return;
                }
                else {


                    Log.d("error", username);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(
                                        LoginActivity.this,
                                        mango.cleanpakistan.MainActivity.class);
                                //intent.putExtra("username", username);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);// Hooray! The user is logged in.
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(
                                                "Login Error")
                                        .setPositiveButton(
                                                android.R.string.ok,
                                                null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                // Signup failed. Look at the ParseException to see what happened.
                            }
                        }
                    });
                }


            }
        });

        TextView tvSignUp = (TextView) findViewById(R.id.tv_signUp);
        tvSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);

            }
        });
    }

    private void initLayout() {
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/BrannbollFet.ttf");

        TextView txtTitle = (TextView) findViewById(R.id.tvAppTitle);
        txtTitle.setTypeface(tf);
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

        return super.onOptionsItemSelected(item);
    }
}
