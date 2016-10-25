package br.com.simplepass.cadevanmotorista.location;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.OnRideActivity;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.utils.Utils;

/**
 * Created by leandro on 4/22/16.
 */
public class PathManager implements PathDeliveringManager {
    private Context mContext;
    private Path mPath;
    private List<Place> mPlaces;
    private Place mGoingToPlace;
    private int mCurrentPosition;
    private Place mInsidePlace;
    private List<Place> mRemovedPlaces;
    private Toast mToast;
    private DistanceToDestinationRequester mDistanceToDestinationRequester;

    public PathManager(Context context, Path path, DistanceToDestinationRequester distanceToDestinationRequester) {
        mContext = context;
        this.mPath = path;
        mPlaces = path.getPlaces();
        mGoingToPlace = path.getPlaces().get(0);
        mCurrentPosition = 0;
        mRemovedPlaces = new ArrayList<>();
        mDistanceToDestinationRequester = distanceToDestinationRequester;
    }

    @Override
    public Path getPath() {
        return mPath;
    }

    @Override
    public boolean nextPlace() {
        while(mCurrentPosition < (mPlaces.size() -1)){
            mCurrentPosition++;

            Place nextPlace = mPlaces.get(mCurrentPosition);

            if(!mRemovedPlaces.contains(nextPlace)){
                mGoingToPlace = nextPlace;

                mDistanceToDestinationRequester.setGoingToPlace(mGoingToPlace);

                mInsidePlace = null;

                Intent intent = new Intent(ACTION_CHANGE_PLACE);
                intent.putExtra(OnRideActivity.PositionChangeReceiver.EXTRA_PLACE_POSITION, mCurrentPosition);

                notifyUser(String.format(mContext.getString(R.string.notify_next_place ),
                        Utils.getNameOfPlace(nextPlace)));

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return true;
            }
        }

        return false;
    }

    @Override
    public void changePlace(int position) {
        if(position < mPlaces.size()){
            mCurrentPosition = position;

            mGoingToPlace = mPlaces.get(position);

            mDistanceToDestinationRequester.setGoingToPlace(mGoingToPlace);

            if(!mDistanceToDestinationRequester.isMonitoring()){
                mDistanceToDestinationRequester.startMonitoring();
            } else{
                mDistanceToDestinationRequester.restartMonitoring();
            }

            Intent intent = new Intent(ACTION_CHANGE_PLACE);
            intent.putExtra(OnRideActivity.PositionChangeReceiver.EXTRA_PLACE_POSITION, mCurrentPosition);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        } else{
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    public void removeFromDelivering(Place place) {
        mRemovedPlaces.add(place);
    }

    @Override
    public boolean isPlaceRemoved(Place place) {
        return mRemovedPlaces.contains(place);
    }

    @Override
    public Place getCurrentPlace() {
        return mGoingToPlace;
    }

    @Override
    public int getCurrentPlaceNumber() {
        return mCurrentPosition;
    }

    @Override
    public void setInsidePlace(Place place) {
        if(place != null) {
            mInsidePlace = place;

            notifyUser(String.format(
                    mContext.getString(R.string.notify_inside_place), Utils.getNameOfPlace(place)));
        }
    }

    @Override
    public Place getInsidePlace() {
        return mInsidePlace;
    }

    @Override
    public Place getPlace(int position) {
        return mPlaces.get(position);
    }

    @Override
    public List<Place> getAllPlaces() {
        return mPlaces;
    }

    private void notifyUser(String message){
        if(mToast != null){
            mToast.cancel();
        }

        mToast = Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG);
        mToast.show();
    }

}
