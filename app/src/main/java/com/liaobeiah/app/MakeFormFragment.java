package com.liaobeiah.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.vpon.ads.VponBanner;

import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.liaobeiah.app.MakeFormActivity}
 * in two-pane mode (on tablets) or a {@link MakeFormActivity}
 * on handsets.
 */
public class MakeFormFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private static final String TAG = "MakeFormFragment";


    private ImageView[] _imageViews;
    private TextView _dateView;
    private TextView _timeView;
    private EditText _licenseView;
    private EditText _locationView;
    private String[] _pictureFilePaths;

    private VponBanner vponBanner = null;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MakeFormFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_make_form, container, false);

        _pictureFilePaths = new String[3];
        int i = 0;
        for (i = 0;i<3;i++) {
            _pictureFilePaths[i] = "";
        }

        _imageViews = new ImageView[3];
        _imageViews[0] = (ImageView)rootView.findViewById(R.id.imageView1);
        _imageViews[1] = (ImageView)rootView.findViewById(R.id.imageView2);
        _imageViews[2] = (ImageView)rootView.findViewById(R.id.imageView3);

        Calendar calendar = Calendar.getInstance();
        TextView dateView = (TextView)rootView.findViewById(R.id.textViewDate);
        dateView.setText(""
            + calendar.get(Calendar.YEAR) + "年"
            + (calendar.get(Calendar.MONTH) + 1) + "月"
            + calendar.get(Calendar.DAY_OF_MONTH) + "日");

        TextView timeView = (TextView)rootView.findViewById(R.id.textViewTime);
        timeView.setText(""
            + calendar.get(Calendar.HOUR_OF_DAY) + "時"
            + calendar.get(Calendar.MINUTE) + "分");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
            R.array.reasons, android.R.layout.simple_list_item_1);

        Spinner spinner = (Spinner)rootView.findViewById(R.id.spinner_reason);
        spinner.setAdapter(adapter);

        // Setup police mail spinner
        Map<String, String> map = ResourceUtils.getHashMapResource(getActivity(), R.xml.police_email);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                getActivity() ,android.R.layout.simple_list_item_1);


        for (String key : map.keySet()) {
            adapter2.add(key);
        }

        spinner = (Spinner)rootView.findViewById(R.id.spinner_receiver);
        spinner.setAdapter(adapter2);


        /*
        RelativeLayout adBannerLayout = (RelativeLayout) rootView.findViewById(R.id.adLayout);
        VponBanner vponBanner = new VponBanner(getActivity(), "8a80818243dca272014423f2acd83c6d", VponAdSize.SMART_BANNER, VponPlatform.TW);
        VponAdRequest adRequest = new VponAdRequest();

        HashSet<String> testDeviceImeiSet = new HashSet<String>();
        testDeviceImeiSet.add(MakeFormFragment.getImei(getActivity())); //填入你那台手機的imei
        adRequest.setTestDevices(testDeviceImeiSet);
        //設定可以auto refresh去要banner
        adRequest.setEnableAutoRefresh(true);
        //開始取得banner
        vponBanner.loadAd(adRequest);
        adBannerLayout.addView(vponBanner);
*/
        return rootView;
    }

    public static String getImei(Context context) {

        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            return imei;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        Log.i(TAG, "onActivityCreated");

        if (savedInstance != null ) {

            //setPictureFilePath(0, savedInstance.getString(FormConstants.PIC_URI_1));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");

        //outState.putString(FormConstants.PIC_URI_1, _pictureFilePaths[0]);
        //outState.putString(FormConstants.PIC_URI_1, _pictureFilePaths[2]);
        //outState.putString(FormConstants.PIC_URI_1, _pictureFilePaths[0]);

    }

    public void reloadEventThumbnail(UUID uuid, int index) {
        File file = FileSystemHelper.getEventThumbnail(getActivity(), uuid, index);
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            _imageViews[index].setImageBitmap(bitmap);
        }
    }

    public void restoreUIState(ContentValues contentValues) {
        UUID uuid = UUID.fromString(contentValues.getAsString(FormConstants.UUID));
        for (int i=0;i<3;i++ ) {
            reloadEventThumbnail(uuid, i);
        }

        // TODO!



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vponBanner != null) {
            //離開時 "千萬"記得要呼叫vponBanner的 destroy
            vponBanner.destroy();
            vponBanner = null;
        }
    }

}



















