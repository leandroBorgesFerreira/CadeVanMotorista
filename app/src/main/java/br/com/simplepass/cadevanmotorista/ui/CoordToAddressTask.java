package br.com.simplepass.cadevanmotorista.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.ProgressShower;
import br.com.simplepass.cadevanmotorista.utils.Utils;

/**
 * Created by leandro on 4/14/16.
 */
public class CoordToAddressTask extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private ProgressShower mProgressShower;
    private Location mLocation;
    private EditText mEditText;


    public CoordToAddressTask(Context context, ProgressShower progressShower, Location location, EditText editText) {
        mContext = context;
        mProgressShower = progressShower;
        mLocation = location;
        mEditText =  editText;
    }

    @Override
    protected String doInBackground(Void... params) {
        if(mLocation == null){
            return null;
        }

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    mLocation.getLatitude(), mLocation.getLongitude(), 1);

            if (addressList == null) {
                return null;
            } else if(addressList.size() < 1){
                return null;
            } else{
                return Utils.addressToString(addressList.get(0));
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        mProgressShower.showProgress(false);
        if(s != null){
            mEditText.setText(s);
        } else {
            Utils.showInfoDialog(mContext, mContext.getString(R.string.dialog_error_place_not_found));
        }
    }
}
