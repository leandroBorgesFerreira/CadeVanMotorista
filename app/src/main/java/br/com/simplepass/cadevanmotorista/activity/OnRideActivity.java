package br.com.simplepass.cadevanmotorista.activity;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.adapters.PlacesOnRideAdapter;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.location.Locator;
import br.com.simplepass.cadevanmotorista.location.PathDeliveringManager;
import io.realm.Realm;

/**
 * OnRideActivity is presented to the user when he is delivering or picking up the students. It show
 * to the driver how the app is doing its job.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 *
 */
public class OnRideActivity extends AppCompatActivity {
    private RecyclerView mPlacesRecyclerView;
    private Realm mRealm;
    private Locator mLocator;
    BroadcastReceiver mPositionChangeReceiver;

    public static final String EXTRA_PATH_ID =
            "br.com.simplepass.cadevanmotorista.activity.EXTRA_PATH_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }

        mRealm = Realm.getDefaultInstance();

        mPlacesRecyclerView = (RecyclerView) findViewById(R.id.places_on_ride_recycler);

        Intent intent = getIntent();

        IntentFilter filter = new IntentFilter();
        filter.addAction(PathDeliveringManager.ACTION_CHANGE_PLACE);
        filter.addAction(PathDeliveringManager.ACTION_PATH_FINISH);
        mPositionChangeReceiver = new PositionChangeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mPositionChangeReceiver, filter);

        if(!intent.hasExtra(EXTRA_PATH_ID)){
            startActivity(new Intent(this, MainActivity.class));
        } else{
            Path path = mRealm.where(Path.class)
                    .equalTo("id", intent.getLongExtra(EXTRA_PATH_ID, -1))
                    .findFirst();

            mLocator = Locator.getInstance();
            mLocator.startDelivering(path);

            PathDeliveringManager pathManager = mLocator.getPathManager();

            PlacesOnRideAdapter adapter = new PlacesOnRideAdapter(this, pathManager);
            mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mPlacesRecyclerView.setAdapter(adapter);
            ((PlacesOnRideAdapter) mPlacesRecyclerView.getAdapter())
                    .highlightPosition(pathManager.getCurrentPlaceNumber());
        }
    }

    @Override
    public void onDestroy(){
        mRealm.close();
        mLocator.stopDelivering();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPositionChangeReceiver);

        super.onDestroy();
    }

    /**
     * PositionChangeReceiver listens for changes in the student that is beeing picked up or delivering.
     * Once a change happens, the list of places is updated.
     *
     * @see android.content.BroadcastReceiver
     */
    public class PositionChangeReceiver extends BroadcastReceiver{
        public static final String EXTRA_PLACE_POSITION =
                "br.com.simplepass.cadevanmotorista.extra.EXTRA_PLACE_POSITION";

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case PathDeliveringManager.ACTION_CHANGE_PLACE:
                    int position = intent.getIntExtra(EXTRA_PLACE_POSITION, 0);

                    ((PlacesOnRideAdapter) mPlacesRecyclerView.getAdapter())
                            .highlightPosition(position);
                    break;
                case PathDeliveringManager.ACTION_PATH_FINISH:
                    ((PlacesOnRideAdapter) mPlacesRecyclerView.getAdapter())
                            .highlightPosition(PlacesOnRideAdapter.NO_HIGHLIGHT);

                    Intent intentBack = new Intent(OnRideActivity.this, MainActivity.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                                OnRideActivity.this);

                        startActivity(intentBack, activityOptions.toBundle());
                    } else {
                        startActivity(intentBack);
                    }
            }
        }
    }

}
