package br.com.simplepass.cadevanmotorista.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import br.com.simplepass.cadevanmotorista.utils.serializers.PathSerializer;
import br.com.simplepass.cadevanmotorista.utils.serializers.PathShareSerializer;
import br.com.simplepass.cadevanmotorista.utils.serializers.PlaceSerializer;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter that show the saved paths.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class PathsAdapter extends RealmBasedRecyclerViewAdapter<Path, PathsAdapter.ViewHolder> {
    private Activity mActivity;
    private ProgressShower mProgressShower;
    private int lastPosition = -1;

    /**
     * @param context Context to be passed in to the class.
     * @param progressShower Progress shower. For the share process.
     * @param realmResults Results of a realm search
     * @param automaticUpdate Automatic Update
     * @param animateResults Animate the results if the information change.
     */
    public PathsAdapter(Activity context, ProgressShower progressShower, RealmResults<Path> realmResults,
                                        boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        mActivity = context;
        this.mProgressShower = progressShower;
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RealmViewHolder {
        @Bind(R.id.container_view_path) View mContainer;
        @Bind(R.id.path_direction) TextView mDirection;
        @Bind(R.id.path_name) TextView mPathName;
        @Bind(R.id.path_share) ImageButton mPathShare;
        @Bind(R.id.path_start) ImageButton mStartPath;

        public ViewHolder(View pathView) {
            super(pathView);
            try {
                ButterKnife.bind(this, pathView);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public void clearAnimation() {
            mContainer.clearAnimation();
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
                .inflate(R.layout.item_list_path, parent, false);

        return new ViewHolder(pathView);
    }

    /**
     * Bind the view and configure the line and the listeners
     * @param holder ViewHolder
     * @param position Current position. This value will vary until the all the lines are iterated.
     */
    @Override
    public void onBindRealmViewHolder(final PathsAdapter.ViewHolder holder, int position) {
        final Path path = realmResults.get(position);

        if(path.getDirection() != null) {
            if (path.getDirection().equals(Path.DIRECTION_SCHOOL)) {
                holder.mDirection.setText(mActivity.getString(R.string.path_go_to));
            } else if (path.getDirection().equals(Path.DIRECTION_HOME)) {
                holder.mDirection.setText(mActivity.getString(R.string.path_come_back));
            }
        }
        
        holder.mPathName.setText(path.getName());

        holder.mPathName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddPathActivity.class);
                intent.putExtra(AddPathActivity.EXTRA_PATH_ID, path.getId());
                intent.putExtra(AddPathActivity.EXTRA_TYPE_OF_INTERACTION, AddPathActivity.TYPE_EDIT);
                intent.putExtra(AddPathActivity.EXTRA_PATH_NAME, path.getName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    FloatingActionButton floatingActionButton =
                            (FloatingActionButton) mActivity.findViewById(R.id.fab);

                    ActivityOptions activityOptions =
                        ActivityOptions.makeSceneTransitionAnimation(mActivity,
                            new Pair<View, String>(holder.mPathName, "toolbar"),
                            new Pair<View, String>(floatingActionButton, mActivity.getString(R.string.transition_fab)));
                    mActivity.startActivity(intent, activityOptions.toBundle());
                } else {
                    mActivity.startActivity(intent);
                }

                mActivity.finish();
            }
        });

        holder.mPathShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AccountManager accountManager = AccountManager.get(mActivity);
                final Account account = new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE);

                final Dialog dialog = new Dialog(mActivity);
                dialog.setContentView(R.layout.dialog_share_path);

                final TextView title = (TextView) dialog.findViewById(R.id.title);
                final Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
                final Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final AutoCompleteTextView sharePhoneAutoComplete =
                        (AutoCompleteTextView) dialog.findViewById(R.id.share_path_autocomplete);

                title.setText(mActivity.getString(R.string.share));

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            List<String> phoneList = new ArrayList<>();

                            phoneList.add(sharePhoneAutoComplete.getText().toString());

                            Gson gson = new GsonBuilder()
                                    .setExclusionStrategies(new ExclusionStrategy() {
                                        @Override
                                        public boolean shouldSkipField(FieldAttributes f) {
                                            return f.getDeclaringClass().equals(RealmObject.class);
                                        }

                                        @Override
                                        public boolean shouldSkipClass(Class<?> clazz) {
                                            return false;
                                        }
                                    })
                                    .registerTypeAdapter(Class.forName("io.realm.PathShareRealmProxy"),
                                            new PathShareSerializer())
                                    .registerTypeAdapter(Class.forName("io.realm.PathRealmProxy"),
                                            new PathSerializer())
                                    .registerTypeAdapter(Class.forName("io.realm.PlaceRealmProxy"),
                                            new PlaceSerializer())
                                    .create();

                            PathShare pathShare =
                                    new PathShare(accountManager.getUserData(account, "name"), path);

                            PushNotification pushNotification = new PushNotification(
                                    phoneList,
                                    gson.toJson(pathShare),
                                    PushMessage.TYPE_SHARE_PATH);

                            CadeVanMotoristaClient client = ServiceGenerator.createService(CadeVanMotoristaClient.class);
                            Call<Void> callPush = client.sendPushNotification(pushNotification);
                            dialog.dismiss();
                            mProgressShower.showProgress(true);
                            callPush.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    mProgressShower.showProgress(false);
                                    if(response.isSuccessful()){
                                        Utils.showInfoDialog(mActivity, mActivity.getString(R.string.path_share_sent));
                                    } else{
                                        Utils.showInfoDialog(mActivity,
                                                mActivity.getString(R.string.error_try_again));
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    mProgressShower.showProgress(false);
                                    Utils.showInfoDialog(mActivity,
                                            mActivity.getString(R.string.error_try_again));
                                }
                            });
                        } catch (ClassNotFoundException e) {
                            Utils.showInfoDialog(mActivity,
                                    mActivity.getString(R.string.error_try_again));
                        }
                    }

                });

                dialog.show();
            }
        });

        holder.mStartPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path.getPlaces().size() > 0) {
                    Intent intent = new Intent(mActivity, OnRideActivity.class);
                    intent.putExtra(OnRideActivity.EXTRA_PATH_ID, path.getId());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                                mActivity);

                        mActivity.startActivity(intent, activityOptions.toBundle());
                    } else {
                        mActivity.startActivity(intent);
                    }
                } else{
                    Utils.showInfoDialog(mActivity, mActivity.getString(R.string.no_places_inside_path));
                }
            }
        });

        setAnimation(holder.mContainer, position);

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

    public Path getItem(int position){
        return realmResults.get(position);
    }

    /**
     * Set animation to present the view holder in a better way every time a new line (or grid) is created.
     * @param viewToAnimate The view that is going to be animated. Probably the container of the line (or grid)
     * @param position The position of the view that is going to be animated.
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
