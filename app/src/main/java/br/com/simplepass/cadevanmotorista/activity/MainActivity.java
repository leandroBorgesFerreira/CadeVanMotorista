package br.com.simplepass.cadevanmotorista.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.adapters.PathsAdapter;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.drawer.DrawerItemClickListener;
import br.com.simplepass.cadevanmotorista.location.Locator;
import br.com.simplepass.cadevanmotorista.retrofit.AccessToken;
import br.com.simplepass.cadevanmotorista.utils.Constants;
import br.com.simplepass.cadevanmotorista.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * First activity after login. Possible to see the the drawer menu. Possible to access the path and to
 * start delivering.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class MainActivity extends AppCompatActivity
        implements ProgressShower{
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private AccessToken mAccessToken;
    private AccountManager mAccountManager;
    private Account mAccount;
    private Realm mRealm;
    private ProgressDialog mProgressDialog;

    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.fab_background) FrameLayout mFabBackgroud;
    @Bind(R.id.fab_add_school_path) FloatingActionButton mFabSchoolPath;
    @Bind(R.id.fab_add_school_path_label) TextView mFabSchoolPathLabel;
    @Bind(R.id.fab_add_home_path) FloatingActionButton mFabHomePath;
    @Bind(R.id.fab_add_home_path_label) TextView mFabHomePathLabel;

    private boolean fabIsOpen;
    private Animation mFabOpenAnim;
    private Animation mFabCloseAnim;
    private Animation mFabRotateFowardAnim;
    private Animation mFabRotateBackwardAnim;

    private static final int ACCESS_LOCATION = 1;

    @Override
    public void showProgress(boolean show){
        if(show){
            mProgressDialog = ProgressDialog.show(this, null,
                    getString(R.string.dialog_wait), true);
        } else{
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }

        mFabSchoolPath.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,
                R.color.material_light_green)));

        mFabHomePath.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,
                R.color.material_red)));

        mFabBackgroud.setClickable(false);
        mFabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        mFabRotateFowardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        mFabRotateBackwardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        mAccountManager = AccountManager.get(MainActivity.this);
        mAccount = new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE);

        drawerInit();

        mRealm = Realm.getDefaultInstance();
        RealmResults<Path> pathRealmResults = mRealm.where(Path.class).findAll();
        pathRealmResults.sort("name");

        PathsAdapter pathsAdapter = new PathsAdapter(this, this, pathRealmResults, true, true);

        RealmRecyclerView pathsView = (RealmRecyclerView) findViewById(R.id.paths_recycler_view);
        pathsView.setAdapter(pathsAdapter);

        AuthAsyncTask authAsyncTask = new AuthAsyncTask();
        authAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * Fab click listener
     * @param view
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
     * Fab (direction) school click listener
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.fab_add_school_path)
    public void addSchoolPath(){
        Intent intent = new Intent(MainActivity.this, AddPathActivity.class);
        intent.putExtra(AddPathActivity.EXTRA_TYPE_OF_PATH, Path.DIRECTION_SCHOOL);

        goToAddPath(intent);
    }

    /**
     * Fab (direction) home click listener
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.fab_add_home_path)
    public void addHomePath(){
        Intent intent = new Intent(MainActivity.this, AddPathActivity.class);
        intent.putExtra(AddPathActivity.EXTRA_TYPE_OF_PATH, Path.DIRECTION_HOME);

        goToAddPath(intent);
    }

    public void goToAddPath(Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this,
                    new Pair<>(findViewById(R.id.fab), getString(R.string.transition_fab)));

            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * This method encapsulates all the animations needed to perform the fab click.
     */
    public void fabOpen(){
        Utils.circularReveal(mFab, mFabBackgroud, false);

        mFabSchoolPath.startAnimation(mFabOpenAnim);
        mFabHomePath.startAnimation(mFabOpenAnim);
        mFab.startAnimation(mFabRotateFowardAnim);

        mFabSchoolPath.setClickable(true);
        mFabHomePath.setClickable(true);

        mFabSchoolPathLabel.setVisibility(View.VISIBLE);
        mFabHomePathLabel.setVisibility(View.VISIBLE);

        fabIsOpen = true;
    }

    /**
     * This method encapsulates all the animations needed to perform the fab hide.
     */
    public void fabClose(){
        Utils.circularReveal(mFab, mFabBackgroud, true);

        mFabSchoolPath.startAnimation(mFabCloseAnim);
        mFabHomePath.startAnimation(mFabCloseAnim);
        mFab.startAnimation(mFabRotateBackwardAnim);

        mFabSchoolPath.setClickable(false);
        mFabHomePath.setClickable(false);

        mFabSchoolPathLabel.setVisibility(View.GONE);
        mFabHomePathLabel.setVisibility(View.GONE);

        fabIsOpen = false;

    }

    @Override
    protected void onDestroy(){
        Locator.getInstance().stop();
        mRealm.close();
        super.onDestroy();
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
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to initialize de drawer menu
     */
    private void drawerInit(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mFab.show();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mFab.hide();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new DrawerItemClickListener(this, mDrawerLayout));

        View headerView = navigationView.getHeaderView(0);

        TextView drawerName = (TextView) headerView.findViewById(R.id.text_view_drawer_name);
        drawerName.setText(mAccountManager.getUserData(mAccount, "name"));

        TextView drawerPhone = (TextView) headerView.findViewById(R.id.text_view_drawer_phone_number);
        drawerPhone.setText(mAccountManager.getUserData(mAccount, "phoneNumber"));

        TextView drawerTrackingCode = (TextView) headerView.findViewById(R.id.text_view_drawer_tracking_code);
        String trackingCode = mAccountManager.getUserData(mAccount, "trackingCode");

        if(trackingCode != null){
            drawerTrackingCode.setText(String.format(getString(R.string.drawer_header_tracking_code), trackingCode));
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        } else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Method to request location access
     *
     * @param view view
     * @return Return if the permission was granted or denied.
     */
    private boolean getLocationPermission(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(view, R.string.permission_rationale_location, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, ACCESS_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, ACCESS_LOCATION);
        }
        return false;
    }

    /**
     * Callback of the permission of location
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Locator.getInstance().start(MainActivity.this);
            }
        }
    }

    /**
     * AsyncTask dedicated to get the oauth token.
     *
     * @see android.os.AsyncTask
     */
    private class AuthAsyncTask extends AsyncTask<Void, Void, AccessToken> {
        @Override
        protected AccessToken doInBackground(Void... params) {
            AccountManagerFuture<Bundle> accountManagerFuture = mAccountManager.getAuthToken(mAccount,
                    "bearer",
                    null,
                    MainActivity.this,
                    null,
                    null);
            Bundle authTokenBundle = null;
            try {
                authTokenBundle = accountManagerFuture.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            }

            if(authTokenBundle != null){
                return new AccessToken(authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString(),
                        "bearer");
            }

            return null;
        }

        @Override
        protected void onPostExecute(AccessToken auth) {
            super.onPostExecute(auth);

            mAccessToken = auth;
            if(getLocationPermission(findViewById(R.id.container_view))){
                Locator.getInstance().start(MainActivity.this);
            }
        }
    }
}
