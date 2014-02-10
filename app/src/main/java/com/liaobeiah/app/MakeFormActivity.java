package com.liaobeiah.app;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link MakeFormFragment}.
 */
public class MakeFormActivity extends FragmentActivity
        implements LocationListener{

    private static int CAMERA_RESULT = 25;
    private static int GALLERY_RESULT = 26;

    public static int FormStateDraft = 0;
    public static int FormStateComplete = 1;

    private static int DIALOG_UNFINISHED_CONFIRM = 1;
    private static String FORM_UUID = "FORM_UUID";

    private static String TAG = "MakeFormActivity";
    private static String MAKE_FORM_FRAGMENT_TAG = "MakeFormFragmentTag";
    private Bundle _formFields;


    private ImageView _currentImageView;
    private int _currentImageIndex;
    private int _formState = FormStateDraft;

    private String mCurrentPhotoPath;

    private LocationManager _locationManager;
    private MakeFormFragment _makeFormFragment;

    private UUID _formUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);


        _formState = FormStateDraft;
        _formFields = new Bundle();
        _locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        _currentImageIndex = -1;

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            _makeFormFragment = new MakeFormFragment();
            _formUuid = UUID.randomUUID();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, _makeFormFragment, MAKE_FORM_FRAGMENT_TAG)
                    .commit();

        } else {
            _makeFormFragment = (MakeFormFragment)getSupportFragmentManager().findFragmentByTag(MAKE_FORM_FRAGMENT_TAG);
            _formUuid = UUID.fromString(savedInstanceState.getString(FORM_UUID));
        }


        _makeFormFragment.setUUID(_formUuid);

    }



    protected void onResume () {
        super.onResume();
        _locationManager.requestSingleUpdate(new Criteria(), this, null);
    }

    protected void onPause() {
        super.onPause();
        _locationManager.removeUpdates(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            showDialog(DIALOG_UNFINISHED_CONFIRM);

            //NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showDialog(DIALOG_UNFINISHED_CONFIRM);
    }


    @Override
    public void onLocationChanged(Location location) {

        Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
        try {
            List<Address> lstAddress = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String returnAddress =lstAddress.get(0).getAddressLine(0);

            EditText editText = (EditText)findViewById(R.id.editTextLocation);
            editText.setText(returnAddress);

        } catch (IllegalArgumentException e) {

        } catch (IOException e) {

        }


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);


        outState.putString(FORM_UUID, _formUuid.toString());

        //outState.putString( FormConstants.PIC_URI_1, _formFields.getString(FormConstants.PIC_URI_1) );
        //outState.putString( FormConstants.PIC_URI_2, _formFields.getString(FormConstants.PIC_URI_2) );
        //outState.putString( FormConstants.PIC_URI_3, _formFields.getString(FormConstants.PIC_URI_3) );
    }


    private void doAbortEditing() {
        // 1. remove pictures
        int i = 0;
        for (i = 0;i<3;i++) {

            String filePath = _makeFormFragment.getPictureFilePath(i);


            File file = new File( filePath );
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle savedInstanced) {
        if (id == DIALOG_UNFINISHED_CONFIRM) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("檢舉表單尚未完成")
                    .setPositiveButton("存成草稿", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();

                        }
                    })
                    .setNegativeButton("饒他一馬", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            doAbortEditing();
                            dialog.dismiss();
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    })
                    .setNeutralButton("繼續編輯", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
        return super.onCreateDialog(id, savedInstanced);
    }


    private String genPictureFilePath(int photoIndex) {
        String fileName = _formUuid.toString() + "-PIC-" + photoIndex + ".jpg";


        File picDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        picDir.mkdir();

        File file = new File(picDir, fileName);
        return file.getAbsolutePath();
    }


    private File createImageFile(int photoIndex) throws IOException {
        // Create an image file name

        String fileName = _formUuid.toString() + "-PIC-" + photoIndex + ".jpg";


        File picDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        picDir.mkdir();

        File file = new File(picDir, fileName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = file.getAbsolutePath();
        return file;
    }

    private void dispatchTakePictureIntent(int photoIndex) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(photoIndex);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_RESULT);
            }
        }
    }

    public void onImageViewClicked(View view){

        _currentImageView = (ImageView)view;
        if (view.getId() == R.id.imageView1) {
            _currentImageIndex = 0;
        } else if (view.getId() == R.id.imageView2) {
            _currentImageIndex = 1;
        } else if (view.getId() == R.id.imageView3) {
            _currentImageIndex = 2;
        } else {
            _currentImageIndex = -1;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.test, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                if ( which == 0 ) {

                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_RESULT);
                } else {
                    dispatchTakePictureIntent(_currentImageIndex);
                }
            }
        });

        builder.show();
    }

    private boolean validateFields() {

        // 1. check picture
        File file = new File( _makeFormFragment.getPictureFilePath(0));
        if (!file.exists()) {
            Toast.makeText(this, "請提供違規照片", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. validate address field
        EditText editText = (EditText)findViewById(R.id.editTextLocation);
        String s = editText.getText().toString();
        if ( s ==null || s.equals("")) {
            Toast.makeText(this, "請輸入違規地點", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }


    public void onSubmitButtonClicked(View view) {

        Log.i(TAG, "onSubmitButtonClicked");
        if (!validateFields()) {
            return;
        }

        _formState = FormStateComplete;


        // Parse Date value.
        TextView dateView = (TextView)findViewById(R.id.textViewDate);
        _formFields.putString(FormConstants.DATE, dateView.getText().toString());

        // Parse Time value
        TextView timeView = (TextView)findViewById(R.id.textViewTime);
        _formFields.putString(FormConstants.TIME, timeView.getText().toString());

        // Parse Reason field
        Spinner spinner = (Spinner)findViewById(R.id.spinner_reason);
        _formFields.putString(FormConstants.REASON, spinner.getSelectedItem().toString());

        // Parse Vehicle License field
        EditText editText = (EditText)findViewById(R.id.editTextVehicleLicense);
        _formFields.putString(FormConstants.VEHICLE_LICENSE, editText.getText().toString());

        Intent intent = new Intent();
        intent.putExtras(_formFields);

        setResult(RESULT_OK, intent);
        finish();


    }

    public void onCancelButtonClicked(View view) {
        // TODO
        showDialog(DIALOG_UNFINISHED_CONFIRM);
        //NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
    }


    public void onDateViewClicked(View view) {

        DatePickerDialog dialog = new DatePickerDialog(this, new PickDate(), 2014, 1, 26);
        dialog.show();
    }

    public void onTimeViewClicked(View view) {
        TimePickerDialog dialog = new TimePickerDialog(this, new PickTime(), 12, 0, false );
        dialog.show();
    }

    private Bitmap loadBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {


            Bitmap photo;
            if ( data == null ) {

                photo = loadBitmap(mCurrentPhotoPath);

                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                //photo = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

            } else {
                photo = (Bitmap) data.getExtras().get("data");
            }

            //=

            // Save Bitmap into file system.
            String filePath = mCurrentPhotoPath;//(String)data.getExtras().get("data");  //savePicture(photo);
            Toast.makeText(this, "FilePath=" + filePath, Toast.LENGTH_LONG).show();

            putPictureFilePath(_currentImageIndex, filePath);
            _makeFormFragment.setPictureFilePath(_currentImageIndex, mCurrentPhotoPath);

            //_currentImageView.setImageBitmap(photo);

        } else if (requestCode == GALLERY_RESULT && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            //cursor.close();

            // Copy external picture into application directory.
            String dstPath = genPictureFilePath(_currentImageIndex);
            putPictureFilePath(_currentImageIndex, dstPath);

            try {
                copy(new File(picturePath), new File(dstPath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            _makeFormFragment.setPictureFilePath(_currentImageIndex, dstPath);
        }
    }

    private void putPictureFilePath(int photoIndex, String filePath) {
        switch ( photoIndex ) {
            case 0:
                _formFields.putString(FormConstants.PIC_URI_1, filePath);
                break;
            case 1:
                _formFields.putString(FormConstants.PIC_URI_2, filePath);
                break;
            case 2:
                _formFields.putString(FormConstants.PIC_URI_3, filePath);
                break;
        }
    }


    private class PickDate implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {



        }

    }


    private class PickTime implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        }
    }























}