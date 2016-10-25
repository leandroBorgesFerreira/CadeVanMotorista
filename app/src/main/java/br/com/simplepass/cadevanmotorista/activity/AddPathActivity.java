package br.com.simplepass.cadevanmotorista.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.adapters.PlacesAdapter;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.dto.LatLngAddress;
import br.com.simplepass.cadevanmotorista.dto.PlaceFormResult;
import br.com.simplepass.cadevanmotorista.dto.StudentFormResult;
import br.com.simplepass.cadevanmotorista.location.Locator;
import br.com.simplepass.cadevanmotorista.retrofit.MyGoogleApiClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse.GoogleGeocodeResponse;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse.Result;
import br.com.simplepass.cadevanmotorista.ui.PlaceForm;
import br.com.simplepass.cadevanmotorista.ui.PlaceFormFactory;
import br.com.simplepass.cadevanmotorista.ui.drag_and_drop.OnStartDragListener;
import br.com.simplepass.cadevanmotorista.ui.drag_and_drop.SimpleItemTouchHelperCallback;
import br.com.simplepass.cadevanmotorista.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Activity dedicada ao cadastro rotas.
 */
public class AddPathActivity extends AppCompatActivity
        implements OnStartDragListener,
                    ProgressShower{
    private Realm mRealm;
    private Path mPath;
    private int mTypeOfInteraction;
    private ItemTouchHelper mItemTouchHelper;
    private PlaceForm mPlaceForm;
    private ProgressDialog mProgressDialog;
    private Menu mMenu;


    @Bind(R.id.form_add_path_container) LinearLayout mForm;
    @Bind(R.id.places_recycler_view) RecyclerView mPlacesRecyclerView;
    @Bind(R.id.path_name_edit_text) EditText mPathNameEditText;

    private boolean fabIsOpen;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.fab_background) FrameLayout mFabBackgroud;
    @Bind(R.id.fab_add_student) FloatingActionButton mFabStudent;
    @Bind(R.id.fab_add_student_label) TextView mFabStudentLabel;
    @Bind(R.id.fab_add_school) FloatingActionButton mFabSchool;
    @Bind(R.id.fab_add_school_label) TextView mFabSchoolLabel;

    private Animation mFabOpenAnim;
    private Animation mFabCloseAnim;
    private Animation mFabRotateFowardAnim;
    private Animation mFabRotateBackwardAnim;

    public final static int ANIMATION_DURATION  = 300;
    public final static String EXTRA_PATH_ID = "br.com.simplepass.cadevanmotorista.EXTRA_PATH_ID";
    public final static String EXTRA_TYPE_OF_INTERACTION =
            "br.com.simplepass.cadevanmotorista.EXTRA_TYPE_OF_INTERACTION";
    public final static String EXTRA_PATH_NAME = "br.com.simplepass.cadevanmotorista.EXTRA_PATH_NAME";
    public final static String EXTRA_TYPE_OF_PATH = "br.com.simplepass.cadevanmotorista.EXTRA_TYPE_OF_PATH";

    public final static int TYPE_EDIT = 0;
    public final static int TYPE_CREATE = 1;
    public static final int ACCESS_CONTACTS = 2;

    /**
     * Méthod to configure the toolbar to show/hide the buttons to register a place.
     * @param isAdding boolean to decide to show or hide the buttons
     */
    private void toolBarAddPlace(boolean isAdding){
        if(isAdding) {
            mMenu.findItem(R.id.menu_confirm_add_place).setVisible(true);
            mMenu.findItem(R.id.menu_confirm_add_path).setVisible(false);
            mMenu.findItem(R.id.menu_delete_path).setVisible(false);
            mMenu.findItem(R.id.menu_cancel_add_place).setVisible(true);
        } else{
            mMenu.findItem(R.id.menu_confirm_add_place).setVisible(false);
            mMenu.findItem(R.id.menu_confirm_add_path).setVisible(true);
            mMenu.findItem(R.id.menu_delete_path).setVisible(true);
            mMenu.findItem(R.id.menu_cancel_add_place).setVisible(false);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void showProgress(boolean show){
        if(show){
            mProgressDialog = ProgressDialog.show(this, null,
                    getString(R.string.dialog_wait), true);
        } else{
            mProgressDialog.dismiss();
        }
    }

    /**
     * Method to request the user permission to access the phone contacts
     * @param view View
     * @return
     */
    private boolean getContactsPermission(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(view, R.string.permission_rationale_location, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, ACCESS_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, ACCESS_CONTACTS);
        }

        return false;
    }

    /**
     * The dialog to confirm if the guessed address if correct. If the address is correct, this mehtod
     * also register the place.
     * @param latLngAddress The latitude, logitude and address based on the address the used has typed.
     * @param placeFormResult The result of what the user has typed
     */
    private void showConfirmAddressDialog(final LatLngAddress latLngAddress, final PlaceFormResult placeFormResult){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        RealmResults<Place> places = mRealm.where(Place.class).findAll();
                        RealmResults<EntityInsidePlace> entities =
                                mRealm.where(EntityInsidePlace.class).findAll();
                        RealmList<Place> placesInPath = mPath.getPlaces();
                        Place place;

                        mRealm.beginTransaction();

                        long lastIdEntity;

                        if(entities.size() > 0){
                            lastIdEntity = entities.max("id").longValue();
                        } else{
                            lastIdEntity = 0;
                        }

                        EntityInsidePlace entityInsidePlace = mRealm.createObject(EntityInsidePlace.class);
                        entityInsidePlace.setId(++lastIdEntity);
                        entityInsidePlace.setName(placeFormResult.getName());
                        entityInsidePlace.setType(placeFormResult.getType());
                        entityInsidePlace.setAddress(placeFormResult.getAddress());

                        if(placeFormResult instanceof StudentFormResult){
                            entityInsidePlace.setCountryCode(((StudentFormResult) placeFormResult).getCountryCode());
                            entityInsidePlace.setPhone(((StudentFormResult) placeFormResult).getPhone());
                            entityInsidePlace.setSchool(((StudentFormResult) placeFormResult).getSchoolName());
                        }

                        if(places.size() > 0){
                            Location addressLocation =
                                    new Location("");
                            addressLocation.setLatitude(latLngAddress.getLat());
                            addressLocation.setLongitude(latLngAddress.getLng());

                            Place closestPlace = null;

                            for(Place placeInPath : placesInPath){
                                Location locationOfPlace = new Location("a");
                                locationOfPlace.setLatitude(placeInPath.getLatitude());
                                locationOfPlace.setLongitude(placeInPath.getLongitude());

                                if(addressLocation.distanceTo(locationOfPlace) < 500){
                                    if(closestPlace == null){
                                        closestPlace = placeInPath;
                                    } else{
                                        Location closestLocation = new Location("b");
                                        locationOfPlace.setLatitude(closestLocation.getLatitude());
                                        locationOfPlace.setLongitude(closestLocation.getLongitude());

                                        if(addressLocation.distanceTo(locationOfPlace) <
                                                addressLocation.distanceTo(closestLocation)){
                                            closestPlace = placeInPath;
                                        }
                                    }
                                }
                            }

                            if(closestPlace != null) {
                                String infoMessage = String.format(Locale.getDefault(),
                                        getString(R.string.dialog_add_close_location),
                                        Utils.getNameOfPlace(closestPlace));

                                Utils.showInfoDialog(AddPathActivity.this, infoMessage);

                                closestPlace.getEntitysInsidePlace().add(entityInsidePlace);

                                mRealm.commitTransaction();
                                hideForm();
                                mPlacesRecyclerView.getAdapter().notifyDataSetChanged();
                                return;
                            }

                            long lastId = places.max("id").longValue();

                            place = new Place();
                            place.setId(++lastId);
                            place.setLatitude(latLngAddress.getLat());
                            place.setLongitude(latLngAddress.getLng());

                            RealmList<EntityInsidePlace> entityList = new RealmList<>();
                            entityList.add(entityInsidePlace);

                            place.setEntitysInsidePlace(entityList);

                            mRealm.copyToRealmOrUpdate(place);
                            placesInPath.add(place);
                        } else{
                            place = mRealm.createObject(Place.class);
                            place.setId(1L);
                            place.setLatitude(latLngAddress.getLat());
                            place.setLongitude(latLngAddress.getLng());

                            RealmList<EntityInsidePlace> entityList = new RealmList<>();
                            entityList.add(entityInsidePlace);

                            place.setEntitysInsidePlace(entityList);

                            mRealm.copyToRealmOrUpdate(place);
                            placesInPath.add(place);
                        }

                        mRealm.commitTransaction();
                        hideForm();
                        mPlacesRecyclerView.getAdapter().notifyDataSetChanged();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPathActivity.this);
        builder.setMessage(latLngAddress.getAddress())
                .setTitle(getString(R.string.dialog_confirm_address))
                .setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_path);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if(toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTypeOfInteraction == TYPE_CREATE) {
                        deletePathDialog(getString(R.string.dialog_confirm_back));
                    } else {
                        onBackPressed();
                    }
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getContactsPermission(findViewById(R.id.container_view));

        Locator.getInstance().start(this);

        mFabBackgroud.setClickable(false);
        mFabStudent.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,
                R.color.material_light_green)));

        mFabSchool.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,
                R.color.material_red)));

        mFabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        mFabRotateFowardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        mFabRotateBackwardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        mRealm = Realm.getDefaultInstance();

        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_TYPE_OF_INTERACTION)){
            mTypeOfInteraction = TYPE_EDIT;
        } else{
            mTypeOfInteraction = TYPE_CREATE;
        }

        if(intent.hasExtra(EXTRA_PATH_ID)){
            mPath = mRealm.where(Path.class)
                    .equalTo("id", intent.getLongExtra(EXTRA_PATH_ID, -1))
                    .findFirst();
            mPathNameEditText.setText(mPath.getName());
        } else {
            RealmResults<Path> paths = mRealm.where(Path.class).findAll();

            long lastPathId;
            if (paths.size() == 0) {
                lastPathId = 0;
            } else {
                lastPathId = paths.last().getId();
            }

            mRealm.beginTransaction();
            mPath = mRealm.createObject(Path.class);
            mPath.setId(++lastPathId);

            if(intent.hasExtra(EXTRA_TYPE_OF_PATH)){
                String direction = intent.getStringExtra(EXTRA_TYPE_OF_PATH);
                mPath.setDirection(direction);
            }

            mRealm.commitTransaction();
        }

        if(intent.hasExtra(EXTRA_PATH_NAME)){
            if(actionBar != null) {
                actionBar.setTitle(intent.getStringExtra(EXTRA_PATH_NAME));
            }
        }

        PlacesAdapter placesAdapter = new PlacesAdapter(this, mPath, this);
        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlacesRecyclerView.setAdapter(placesAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(placesAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mPlacesRecyclerView);
    }

    /**
     * Method to start the process of saving a place.
     */
    private void savePlace(){
        if(mPlaceForm.isCorrect()) {
            showProgress(true);

            FindPlacesTask findPlacesTask = new FindPlacesTask(this, mPlaceForm);
            findPlacesTask.execute(mPlaceForm.getPlace().getAddress());
        }
    }

    /**
     * Fab click listener
     * @param view view
     */
    @SuppressWarnings("unused")
    @OnClick({R.id.fab, R.id.fab_background})
    public void fabClick(View view){
        if(!fabIsOpen){
            fabOpen();
        } else{
            fabClose();
        }
    }

    /**
     * Fab student click listener
     * @param view view
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.fab_add_student)
    public void fabStudentClick(View view){
        showForm(PlaceForm.STUDENT);
    }

    /**
     * Fab school click listener
     * @param view view
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.fab_add_school)
    public void fabSchoolClick(View view){
        showForm(PlaceForm.SCHOOL);
    }

    /**
     * Method to encapsulate all the animations of showing the FAB
     */
    public void fabOpen(){
        /*mFabBackgroud.setVisibility(View.VISIBLE);
        mFabBackgroud.setClickable(true);*/

        Utils.circularReveal(mFab, mFabBackgroud, false);

        mFabStudent.startAnimation(mFabOpenAnim);
        mFabSchool.startAnimation(mFabOpenAnim);
        mFab.startAnimation(mFabRotateFowardAnim);

        mFabStudent.setClickable(true);
        mFabSchool.setClickable(true);

        mFabStudentLabel.setVisibility(View.VISIBLE);
        mFabSchoolLabel.setVisibility(View.VISIBLE);

        fabIsOpen = true;
    }

    /**
     * Method to encapsula all the animations of the closing FAB
     */
    public void fabClose(){
        /*mFabBackgroud.setVisibility(View.INVISIBLE);
        mFabBackgroud.setClickable(false);*/

        Utils.circularReveal(mFab, mFabBackgroud, true);

        mFabStudent.startAnimation(mFabCloseAnim);
        mFabSchool.startAnimation(mFabCloseAnim);
        mFab.startAnimation(mFabRotateBackwardAnim);

        mFabStudent.setClickable(false);
        mFabSchool.setClickable(false);

        mFabStudentLabel.setVisibility(View.GONE);
        mFabSchoolLabel.setVisibility(View.GONE);

        fabIsOpen = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_path, menu);

        mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.menu_confirm_add_path:

                String pathName =  mPathNameEditText.getText().toString();

                if(pathName.isEmpty()){
                    Toast.makeText(this, "Escolha um nome para a rota!", Toast.LENGTH_SHORT).show();
                    break;
                }

                mRealm.beginTransaction();
                mPath.setName(pathName);

                mRealm.copyToRealmOrUpdate(mPath);
                mRealm.commitTransaction();

                Intent intentBack = new Intent(AddPathActivity.this, MainActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            AddPathActivity.this);
                    startActivity(intentBack, activityOptions.toBundle());
                } else {
                    startActivity(intentBack);
                    finish();
                }
                break;
            case R.id.menu_delete_path:
                deletePathDialog(getString(R.string.dialog_confirm_delete_path));
                break;
            case R.id.menu_cancel_add_place:
                hideForm();
                break;
            case R.id.menu_confirm_add_place:
                savePlace();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mForm.getVisibility() == View.VISIBLE){
            hideForm();
        } else {
            if(mTypeOfInteraction == TYPE_CREATE) {
                deletePathDialog(getString(R.string.dialog_confirm_back));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            AddPathActivity.this,
                            new Pair<>(findViewById(R.id.fab), getString(R.string.transition_fab)));

                    Intent intent = new Intent(AddPathActivity.this, MainActivity.class);
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(new Intent(AddPathActivity.this, MainActivity.class));
                }
            }
        }
    }

    /**
     * Method to delete the Path
     * @param msg Message to display in the dialog to warn the user about the delete
     */
    private void deletePathDialog(String msg){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mRealm.beginTransaction();
                        mPath.removeFromRealm();
                        mPath = null;
                        mRealm.commitTransaction();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                                AddPathActivity.this,
                                new Pair<>(findViewById(R.id.fab), getString(R.string.transition_fab)));

                            Intent intent = new Intent(AddPathActivity.this, MainActivity.class);
                            startActivity(intent, activityOptions.toBundle());
                        } else {
                            startActivity(new Intent(AddPathActivity.this, MainActivity.class));
                        }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener)
                .show();
    }

    /**
     * Method to show the form to add a place. The method encapsulate the animations to hide the form
     * @param type is the kind of form... Student, school... it varies with the Fab clicked
     */
    private void showForm(int type) {
        fabClose();
        mFab.hide();
        mFab.setClickable(false);

        toolBarAddPlace(true);

        ScrollView scrollView = (ScrollView) findViewById(R.id.form_scroll);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

        mForm.setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int finalRadius = Math.max(mForm.getWidth(), mForm.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(mForm, (int) mFab.getX() + 5,
                    mForm.getBottom() - 70, 0, finalRadius);

            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        Animation fadeOut = AnimationUtils.loadAnimation(AddPathActivity.this, R.anim.fade_out);

        mPlacesRecyclerView.startAnimation(fadeOut);
        mPlacesRecyclerView.setVisibility(View.INVISIBLE);

        mPlaceForm = PlaceFormFactory.createPlaceForm(this,
                this,
                (LinearLayout)findViewById(R.id.form_add_path_place),
                type);

        mPlaceForm.insertForm();
    }

    /**
     * Method to encapsulate all the animations to hide the form.
     */
    private void hideForm(){
        mFab.show();
        mFab.setClickable(true);
        mForm.setVisibility(View.GONE);

        toolBarAddPlace(false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int initialRadius = mForm.getWidth();
            Animator animator = ViewAnimationUtils.createCircularReveal(mForm, (int) mFab.getX() + 5,
                    mForm.getBottom() - 70, initialRadius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    Animation fadeIn = AnimationUtils.loadAnimation(AddPathActivity.this, R.anim.fade_in);

                    mPlacesRecyclerView.startAnimation(fadeIn);
                    mPlacesRecyclerView.setVisibility(View.VISIBLE);

                    mPlaceForm.removeForm();
                }
            });

            animator.start();
        } else{
            Animation fadeIn = AnimationUtils.loadAnimation(AddPathActivity.this, R.anim.fade_in);

            mPlacesRecyclerView.startAnimation(fadeIn);
            mPlacesRecyclerView.setVisibility(View.VISIBLE);
            mPlaceForm.removeForm();
        }
    }

    /**
     * Async Task @see android.os.AsyncTask dedicated to find the addess based on what the user has
     * typed
     *
     */
    private class FindPlacesTask extends AsyncTask<String, Void, LatLngAddress>{
        private Context mmContext;
        PlaceForm mmPlaceForm;

        public FindPlacesTask(Context context, PlaceForm placeForm) {
            mmContext = context;
            this.mmPlaceForm = placeForm;
        }

        /**
         * This method finds the address based on a string input
         * @param stringAddress string of the address that will be used to find the correct address
         * @return the latitude, longitude e string of the found address
         * @throws IOException
         */
        private LatLngAddress addressToCoordByGoogleApi(String stringAddress) throws IOException{
            MyGoogleApiClient myGoogleApiClient =
                    ServiceGenerator.createServiceForGoogleAPIs(MyGoogleApiClient.class);

            Call<GoogleGeocodeResponse> callGeocode = myGoogleApiClient.getPositionFromAddress(
                    MyGoogleApiClient.TYPE_OF_ANSWER_JSON,
                    stringAddress,
                    MyGoogleApiClient.PORTUGUESE_BRAZIL,
                    getString(R.string.google_services_key));

            Response<GoogleGeocodeResponse> response = callGeocode.execute();
            if(response.isSuccessful() && response.body().getStatus().equals("OK")) {
                Result result = response.body().getResults().get(0);

                return new LatLngAddress(result.getFormatted_address(),
                        result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng());


            } else{
                return null;
            }
        }

        @Override
        protected LatLngAddress doInBackground(String... params) {
            String stringAddress = params[0];

            Geocoder geocoder = new Geocoder(AddPathActivity.this, Locale.getDefault());
            List<Address> addressList;

            try {
                Location location = Locator.getInstance().getLastLocation();
                if(location != null) {
                    addressList = geocoder.getFromLocationName(stringAddress,
                            1,
                            (location.getLatitude() - 0.8),
                            (location.getLongitude() - 0.8),
                            (location.getLatitude() + 0.8),
                            (location.getLongitude() + 0.8));
                } else{
                    addressList = geocoder.getFromLocationName(stringAddress, 1);
                }

                if (addressList == null) {
                    return addressToCoordByGoogleApi(stringAddress);
                } else if(addressList.size() < 1){
                    return addressToCoordByGoogleApi(stringAddress);
                } else{
                    Address address = addressList.get(0);

                    return new LatLngAddress(Utils.addressToString(address),
                            address.getLatitude(),
                            address.getLongitude());
                }
            } catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(LatLngAddress latLngAddress) {
            showProgress(false);
            if(latLngAddress != null) {
                showConfirmAddressDialog(latLngAddress, mmPlaceForm.getPlace());
            } else{
                Utils.showInfoDialog(mmContext, getString(R.string.dialog_error_place_not_found));
            }
        }
    }
}
