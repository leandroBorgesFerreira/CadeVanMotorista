package br.com.simplepass.cadevanmotorista.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.adapters.EntitiesInPlaceAdapter;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class EditPlaceActivity extends AppCompatActivity
    implements PlaceEditor{
    private Realm mRealm;
    private Intent mIntent;
    private boolean mHasDataToSave;
    private Path mPath;

    @Bind(R.id.entities_in_place_recycler_view) RecyclerView mEntitiesRecyclerView;

    private EntityInsidePlace mEntityOnFocus;
    private EditText mEditTextOnFocus;
    private int mTypeOfEditText;

    public static final String EXTRA_PLACE_ID =
            "br.com.simplepass.cadevanmotorista.extra.EXTRA_PLACE_ID";

    public static final int TYPE_NAME = 1;
    public static final int TYPE_PHONE = 2;
    public static final int TYPE_SCHOOL = 3;

    @Override
    public boolean splitEntity(Place place, EntityInsidePlace entityInsidePlace) {
        if(place.getEntitysInsidePlace().size() == 1){
            Snackbar.make(findViewById(R.id.cordinator_layout),
                    getString(R.string.only_one_entity), Snackbar.LENGTH_LONG).show();
            return false;
        }

        RealmList<EntityInsidePlace> entities = new RealmList<>();
        mRealm.beginTransaction();

        for(EntityInsidePlace entity : place.getEntitysInsidePlace()){
            if(entity.equals(entityInsidePlace)){
                entities.add(entityInsidePlace);
                place.getEntitysInsidePlace().remove(entity);
            }
        }

        RealmResults<Place> places = mRealm.where(Place.class).findAll();
        long lastId = places.max("id").longValue();

        Place newPlace = new Place(++lastId,
                place.getLatitude(),
                place.getLongitude(),
                entities);

        mPath.getPlaces().add(newPlace);
        mRealm.commitTransaction();

        View view = findViewById(R.id.cordinator_layout);

        if(view != null) {
            Snackbar.make(view , getString(R.string.entity_splited), Snackbar.LENGTH_LONG).show();
        } else{
            Toast.makeText(this, getString(R.string.entity_splited), Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mIntent = getIntent();

        mHasDataToSave = false;

        if(toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backActivity(mIntent);
                }
            });
        }

        mRealm = Realm.getDefaultInstance();

        if(mIntent.hasExtra(AddPathActivity.EXTRA_PATH_ID)){
            mIntent.putExtra(AddPathActivity.EXTRA_PATH_ID,
                    mIntent.getLongExtra(AddPathActivity.EXTRA_PATH_ID, -1));
            mPath = mRealm.where(Path.class)
                    .equalTo("id", mIntent.getLongExtra(AddPathActivity.EXTRA_PATH_ID, -1))
                    .findFirst();
        } else{
            finish();
        }

        if(mIntent.hasExtra(EXTRA_PLACE_ID)){
            long placeId = mIntent.getLongExtra(EXTRA_PLACE_ID, -1);

            Place place = mRealm.where(Place.class)
                    .equalTo("id", placeId)
                    .findFirst();
            actionBar.setTitle(Utils.getNameOfPlace(place));

            mEntitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mEntitiesRecyclerView.setAdapter(new EntitiesInPlaceAdapter(place, this));

        } else{
            finish();
        }
    }

    private void backActivity(Intent intent){
        if(mHasDataToSave) {
            saveEditTextInFocus(mEntityOnFocus, mEditTextOnFocus, mTypeOfEditText);
        }

        Intent intentBack = new Intent(EditPlaceActivity.this, AddPathActivity.class);
        intentBack.putExtra(AddPathActivity.EXTRA_TYPE_OF_INTERACTION, AddPathActivity.TYPE_EDIT);

        if(intent.hasExtra(AddPathActivity.EXTRA_PATH_ID)){
            intentBack.putExtra(AddPathActivity.EXTRA_PATH_ID,
                    intent.getLongExtra(AddPathActivity.EXTRA_PATH_ID, -1));
        }

        startActivity(intentBack);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.menu_confirm_add_path:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        mRealm.close();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backActivity(mIntent);
    }

    @Override
    public void getEditTextInFocus(EntityInsidePlace entity, EditText editText, int type) {
        mHasDataToSave = true;
        mEntityOnFocus = entity;
        mEditTextOnFocus = editText;
        mTypeOfEditText = type;
    }

    @Override
    public void saveEditTextInFocus(EntityInsidePlace entity, EditText editText, int type) {
        mRealm.beginTransaction();
        switch (type){
            case TYPE_NAME:
                entity.setName(editText.getText().toString());
                break;
            case TYPE_PHONE:
                entity.setPhone(editText.getText().toString());
                break;
            case TYPE_SCHOOL:
                entity.setSchool(editText.getText().toString());
                break;
        }
        mRealm.commitTransaction();
    }
}
