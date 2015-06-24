package com.tag.ddamianow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.takeimage.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

@SuppressWarnings("ALL")
public class MainActivity extends Activity {

    public final static int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;
	public Button btnSelect;
	public ImageView ivImage;
    public TessBaseAPI real = new TessBaseAPI();
    public ImageFix fix = new ImageFix();
    public String selectedImagePath;
    public String result;
    public String toSendResult;
    private ProgressDialog dialog;
    public ImageTag imageOffTag;
    public ImageTag imageOnTag;


    @Override
	protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        /* tags used for image existance checks atm*/
        imageOffTag = new ImageTag();
        imageOffTag.whatAmI = "nothing";
        imageOnTag = new ImageTag();
        imageOnTag.whatAmI = "something";
        /* tags */

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setTag(imageOffTag);

        // creates the tessdata folder and copies the language file there
        TessData.initTessTrainedData(this);




    }


    private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);*/
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator + "TextImage.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        //Check that request code matches ours:


		if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
            {
                //Get our saved file into a bitmap object:
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "TextImage.jpg");
                Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
                ivImage.setImageBitmap(bitmap);
                ivImage.setTag(imageOnTag);

            }
			if (requestCode == SELECT_FILE)
				onSelectFromGalleryResult(data);
			/*else if (requestCode == REQUEST_CAMERA)
				onCaptureImageResult(data);*/
		}
	}
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bm;


        BitmapFactory.decodeFile(path, options);
        final int REQUIRED_SIZE = 400;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;


        Bitmap bitmapR = BitmapFactory.decodeFile(path, options);

        /*// Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmapR = BitmapFactory.decodeFile(path, options);*/
        return bitmapR;
    }

	/*private void onCaptureImageResult(Intent data) {

		Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		File destination = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".jpg");

		FileOutputStream fo;
		try {
			destination.createNewFile();
			fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ivImage.setImageBitmap(thumbnail);
        ivImage.setTag(imageOnTag);
        // making the buttons be on top of the image
        findViewById(R.id.buttonsLayout).bringToFront();
	}*/

	@SuppressWarnings("deprecation")
	private void onSelectFromGalleryResult(Intent data) {
		Uri selectedImageUri = data.getData();
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
				null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		selectedImagePath = cursor.getString(column_index);

		Bitmap bm;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(selectedImagePath, options);
		final int REQUIRED_SIZE = 400;
		int scale = 1;
		while (options.outWidth / scale / 2 >= REQUIRED_SIZE
				&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
			scale *= 2;
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(selectedImagePath, options);

		ivImage.setImageBitmap(bm);
        ivImage.setTag(imageOnTag);
        // making the buttons be on top of the image
        findViewById(R.id.buttonsLayout).bringToFront();
	}

    public String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    // the method used when OCR button is clicked
    public void doOCRStuff(View view){
        ImageTag test = (ImageTag) ivImage.getTag();
        // check if image is loaded
        if(test.whatAmI.equals("something")) {
            new doOCRloader().execute();
        } else {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
        }
    }

    // inner AsyncTask with a loader dialog for the image processing
    private class doOCRloader extends AsyncTask<Void, Void, Boolean> {

        protected void onPreExecute (){
            dialog = ProgressDialog.show(MainActivity.this, getString(R.string.loading), getString(R.string.loading_please_wait), true);
            /* locks screen orientation */
            lockOrientation(MainActivity.this);
        }
        @Override
        protected Boolean doInBackground(Void... params) {

            real.init(TessData.getTesseractFolder(), "bul");

            Bitmap bitmap = ((BitmapDrawable)ivImage.getDrawable()).getBitmap();
            // fix.fixOrientation(selectedImagePath, bitmap); // does nothing right now
            real.setImage(bitmap);
            String test = real.getUTF8Text();
            toSendResult = test;
            //result = convertFromUTF8(test);
            result = test;

            real.end();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            dialog.hide();
            dialog.dismiss();
            sendMessage(getWindow().getDecorView().findViewById(android.R.id.content));

            /* unlocks screen rotation */
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    // sending the result to the result activity to display the processed text
    public void sendMessage(View view) {
        Intent intent = new Intent(this, TextResultActivity.class);
        String message = toSendResult;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /* locks screen orientation */
    public static void lockOrientation(Activity activity) {
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int tempOrientation = activity.getResources().getConfiguration().orientation;
        int orientation = 0;
        switch(tempOrientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }
        activity.setRequestedOrientation(orientation);
    }





}
