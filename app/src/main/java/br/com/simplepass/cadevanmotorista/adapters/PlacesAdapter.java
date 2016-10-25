package br.com.simplepass.cadevanmotorista.adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.AddPathActivity;
import br.com.simplepass.cadevanmotorista.activity.EditPlaceActivity;
import br.com.simplepass.cadevanmotorista.activity.OnRideActivity;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.ui.drag_and_drop.ItemTouchHelperAdapter;
import br.com.simplepass.cadevanmotorista.ui.drag_and_drop.OnStartDragListener;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmViewHolder;

/**
 * Adapter that show and allow editions in the a place.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private Activity mActivity;
    private List<Place> mPlaces;
    private Path mPath;
    private OnStartDragListener mDragStartListener;

    /**
     *
     * @param activity The activity the created this adapter
     * @param path path that the places are inside.
     * @param dragStartListener The drag and drop listener. This is needed to make possible the
     *                          action to resort the list of places.
     */
    public PlacesAdapter(Activity activity, Path path, OnStartDragListener dragStartListener) {
        mPath = path;
        this.mActivity = activity;
        mPlaces = path.getPlaces();
        mDragStartListener = dragStartListener;
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RealmViewHolder {
        @Bind(R.id.place_name) TextView mPlaceName;
        //public TextView mPlacePhone;
        @Bind(R.id.place_move_image) ImageView mDragAndDropImageView;
        @Bind(R.id.place_edit_button) ImageButton mBtnEdit;
        @Bind(R.id.place_delete_button) ImageButton mBtnDelete;
        @Bind(R.id.container_view_place) View mContainerView;

        public ViewHolder(View placeView) {
            super(placeView);

            try {
                ButterKnife.bind(this, placeView);
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View pathView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_place, parent, false);

        return new ViewHolder(pathView);
    }

    /**
     * Bind the view and configure the line and the listeners
     * @param holder ViewHolder
     * @param position Current position. This value will vary until the all the lines are iterated.
     */
    @Override
    public void onBindViewHolder(final PlacesAdapter.ViewHolder holder, int position) {
        final Place place = mPlaces.get(position);

        if(position % 2 == 0){
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mActivity,
                    R.color.list_item_color1));
        } else{
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mActivity,
                    R.color.list_item_color2));
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(EntityInsidePlace entity : place.getEntitysInsidePlace()){
            stringBuilder.append(entity.getName()).append(", ");
        }

        holder.mPlaceName.setText(stringBuilder.toString());

        holder.itemView.setTag(place);

        holder.mDragAndDropImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }

                return true;
            }
        });

        holder.mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EditPlaceActivity.class);
                intent.putExtra(EditPlaceActivity.EXTRA_PLACE_ID, place.getId());
                intent.putExtra(AddPathActivity.EXTRA_PATH_ID, mPath.getId());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            mActivity, new Pair<View, String>(holder.mPlaceName, "toolbar"));

                    mActivity.startActivity(intent, activityOptions.toBundle());
                } else {
                    mActivity.startActivity(intent);
                }

                mActivity.finish();
            }
        });

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                place.removeFromRealm();
                                realm.commitTransaction();
                                realm.close();

                                notifyItemRemoved(holder.getAdapterPosition());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getString(R.string.dialog_confirm_delete_place))
                        .setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener)
                        .show();
            }
        });

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mPlaces, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mPlaces, i, i - 1);
            }
        }

        realm.commitTransaction();
        realm.close();
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
    }

    /**
     * Get item count. The adapter will iterate over the lines based on this value
     * @return the size of the paths
     */
    @Override
    public int getItemCount() {
        try {
            return mPlaces.size();
        } catch (IllegalStateException e) {
            return 0;
        }

    }

    public Place getItem(int position){
        return mPlaces.get(position);
    }

}
