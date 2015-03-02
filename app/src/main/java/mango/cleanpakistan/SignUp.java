package mango.cleanpakistan;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUp extends ActionBarActivity {

    Button Signup;
    EditText user,email,pwd,location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initLayout();
        user=(EditText) findViewById(R.id.etuser);
        email=(EditText) findViewById(R.id.etemail);
        pwd=(EditText) findViewById(R.id.etpwd);
        location=(EditText) findViewById(R.id.etloc);

        Signup = (Button) findViewById(R.id.btsignup);

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString();
                String password = pwd.getText().toString();
                String email1 = email.getText().toString();
                String location1 = location.getText().toString();

                username = username.trim();
                password = password.trim();
                username = username.trim();
                email1 = email1.trim();
                location1 = location1.trim();

                if (username.isEmpty() || password.isEmpty() || email1.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            SignUp.this);
                    builder.setMessage("Sign Up Error")
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // create the new user!
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email1);
                    newUser.put("Location", location1);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                // Success!
                                Intent intent = new Intent(SignUp.this,
                                        LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        SignUp.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle("Error Sign Up")
                                        .setPositiveButton(android.R.string.ok,
                                                null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }

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
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
