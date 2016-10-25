package br.com.simplepass.cadevanmotorista.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.AddPathActivity;
import br.com.simplepass.cadevanmotorista.activity.OnRideActivity;
import br.com.simplepass.cadevanmotorista.activity.ProgressShower;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.dto.PathShare;
import br.com.simplepass.cadevanmotorista.dto.PushMessage;
import br.com.simplepass.cadevanmotorista.dto.PushNotification;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.utils.Constants;
import br.com.simplepass.cadevanmotorista.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter that shows all the shared paths.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class PathsSharedAdapter extends RealmBasedRecyclerViewAdapter<PathShare, PathsSharedAdapter.ViewHolder> {
    private Activity mActivity;

    public PathsSharedAdapter(Activity context, RealmResults<PathShare> realmResults,
                              boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        mActivity = context;
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RealmViewHolder {
        @Bind(R.id.path_share_id) TextView mId;
        @Bind(R.id.shared_by) TextView mSharedBy;
        @Bind(R.id.path_share_direction) TextView mDirection;
        @Bind(R.id.path_share_name) TextView mPathName;
        @Bind(R.id.path_share_accept) ImageButton mPathShareAccept;
        @Bind(R.id.path_share_delete) ImageButton mPathShareDelete;
        @Bind(R.id.container_view) View mContainerView;

        public ViewHolder(View pathView) {
            super(pathView);
            try {
                ButterKnife.bind(this, pathView);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Method responsable to create the view holder
     *
     * @param parent ViewGroup
     * @param viewType view typer
     *  @return the ViewHolder
     */
    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup parent, int viewType) {
        View pathView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_path_share, parent, false);

        return new ViewHolder(pathView);
    }

    /**
     * Bind the view and configure the line and the listeners
     * @param holder ViewHolder
     * @param position Current position. This value will vary until the all the lines are iterated.
     */
    @Override
    public void onBindRealmViewHolder(final PathsSharedAdapter.ViewHolder holder, int position) {
        final PathShare pathShare = realmResults.get(position);
        final Path path = pathShare.getPath();

        if(position % 2 == 0){
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mActivity,
                    R.color.list_item_color1));
        } else{
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mActivity,
                    R.color.list_item_color2));
        }

        if(path.getDirection() != null) {
            if (path.getDirection().equals(Path.DIRECTION_SCHOOL)) {
                holder.mDirection.setText(mActivity.getString(R.string.path_go_to));
            } else if (path.getDirection().equals(Path.DIRECTION_HOME)) {
                holder.mDirection.setText(mActivity.getString(R.string.path_come_back));
            }
        }

        holder.mId.setText(String.valueOf(path.getId()));
        holder.mSharedBy.setText(pathShare.getDriverName());
        holder.mPathName.setText(path.getName());

        holder.mPathShareAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "Implementar", Toast.LENGTH_SHORT).show();
            }
        });

        holder.mPathShareDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathShare.removeFromRealm();
            }
        });

        holder.itemView.setTag(path);
    }

    /**
     * Get item count. The adapter will iterate over the lines based on this value
     * @return the size of the paths
     */
    @Override
    public int getItemCount() {
        return realmResults.size();
    }

}
