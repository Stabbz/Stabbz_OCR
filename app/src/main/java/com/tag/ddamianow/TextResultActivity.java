package com.tag.ddamianow;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takeimage.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class TextResultActivity extends ActionBarActivity {

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Set the text view as the activity layout
        setContentView(R.layout.activity_text_result);
        // Create the text view
        TextView textView = (TextView) findViewById(R.id.textView_result);
        // Typeface font = Typeface.createFromAsset(getApplication().getAssets(), "DejaVuSans.ttf");

        textView.setText(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_result, menu);
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



    // onClick method for the save to text file button
    public void saveTextToFile(View view){
        if(isExternalStorageWritable()){
            // TO-DO: make file names
            generateTextOnSD("Saved Text", message);
        }
    }

    // method to save resulting text to a file on sd card
    // NB: check if card is mounted etc.. when using method
    void generateTextOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Recognised Text");
            //File root = new File(Environment.getExternalStoragePublicDirectory(
            //        Environment.DIRECTORY_PICTURES), "Recognised Text");
            if (!root.exists()) {
                root.mkdirs();
            }

            /* save file with .txt extension */
            File ocrfile = new File(root, sFileName + ".txt");
            FileWriter writer = new FileWriter(ocrfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /* used to send the Saved Text file to the users current email */


    public void sendFileToUserEmail(View view){

        /* check if user is online */
        if (isOnline()) {
            String userEmail = UserEmailFetcher.getEmail(getApplicationContext());
            /* check if there is a logged in email */
            if(userEmail != null) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    // set the type to 'email'
                    emailIntent.setType("vnd.android.cursor.dir/email");
                    String to[] = {userEmail};
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                    // the mail subject
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Recognised Text");
                    // the attachment
                    String root = Environment.getExternalStorageDirectory()  + "/Recognised Text/";
                    File file = new File(root, "Saved Text.txt");
                    if (!file.exists() || !file.canRead()) {
                        Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    Uri uri = Uri.fromFile(file);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No email added to device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to the Internet.", Toast.LENGTH_SHORT).show();
        }
    }

    /* Checks if phone is connected to the internet */
    boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
