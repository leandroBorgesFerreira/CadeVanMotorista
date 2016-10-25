package br.com.simplepass.cadevanmotorista.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.adapters.PathsAdapter;
import br.com.simplepass.cadevanmotorista.adapters.PathsSharedAdapter;
import br.com.simplepass.cadevanmotorista.dto.PathShare;
import butterknife.Bind;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Class to see the paths that other drivers shared with the user.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class SharedPathsActivity extends AppCompatActivity {
    @Bind(R.id.paths_shared_recycler_view)
    RealmRecyclerView mSharedPathsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_paths);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<PathShare> pathShareRealmResults = realm.where(PathShare.class).findAll();

        if(pathShareRealmResults.size() > 0) {
            PathsSharedAdapter pathsAdapter =
                    new PathsSharedAdapter(this, pathShareRealmResults, true, true);
            mSharedPathsRecyclerView.setAdapter(pathsAdapter);
        }

    }

}
