package br.com.simplepass.cadevanmotorista.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.location.DistanceToDestinationRequester;
import br.com.simplepass.cadevanmotorista.location.Locator;
import br.com.simplepass.cadevanmotorista.location.PathDeliveringManager;
import br.com.simplepass.cadevanmotorista.utils.Constants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter that show and allow interactions in the path of the deliver.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class PlacesOnRideAdapter extends RecyclerView.Adapter<PlacesOnRideAdapter.ViewHolder>{
    private Context mContext;
    private PathDeliveringManager mPathDeliveringManager;
    private Place mHighLightPlace;

    public static final int NO_HIGHLIGHT = -1;

    public PlacesOnRideAdapter(Context mContext, PathDeliveringManager pathDeliveringManager) {
        mPathDeliveringManager = pathDeliveringManager;
        this.mContext = mContext;
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.status_image_view) ImageView mStatusImage;
        @Bind(R.id.place_on_ride_name) TextView mPlaceName;
        @Bind(R.id.place_remove_from_delivering) ImageView mRemovePlaceImage;
        @Bind(R.id.container_view_place_ride) View mContainerView;

        public ViewHolder(View view) {
            super(view);

            try {
                ButterKnife.bind(this, view);
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
        View placeOnRideView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_place_on_ride, parent, false);

        return new ViewHolder(placeOnRideView);
    }

    /**
     * Bind the view and configure the line and the listeners
     * @param holder ViewHolder
     * @param position Current position. This value will vary until the all the lines are iterated.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Place place = mPathDeliveringManager.getPlace(position);

        if(place.equals(mHighLightPlace)){
            holder.mStatusImage.setImageResource(R.mipmap.ic_access_time);
        } else{
            holder.mStatusImage.setImageDrawable(null);
        }

        if (position % 2 == 0) {
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mContext,
                    R.color.list_item_color1));
        } else {
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mContext,
                    R.color.list_item_color2));
        }

        if(mPathDeliveringManager.isPlaceRemoved(place)){
            holder.mContainerView.setBackgroundColor(ContextCompat.getColor(mContext,
                    R.color.material_red_light));
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(EntityInsidePlace entity : place.getEntitysInsidePlace()){
            stringBuilder.append(entity.getName()).append(", ");
        }

        holder.mPlaceName.setText(stringBuilder.toString());

        holder.mRemovePlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPathDeliveringManager.removeFromDelivering(place);
                notifyDataSetChanged();
            }
        });

        holder.mContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceToDestinationRequester distanceToDestinationRequester =
                        Locator.getInstance().getDistanceToDestinationRequester();

                if(!distanceToDestinationRequester.isMonitoring()){
                    distanceToDestinationRequester.startMonitoring();
                } else{
                    distanceToDestinationRequester.restartMonitoring();
                }

                mPathDeliveringManager.setInsidePlace(null);
                mPathDeliveringManager.changePlace(holder.getAdapterPosition());
            }
        });

    }

    /**
     * Method to highlight a position so the driver knows where the app think he is going now.
     * @param position
     */
    public void highlightPosition(int position){
        if(position == NO_HIGHLIGHT){
            mHighLightPlace = null;
        } else {
            mHighLightPlace = mPathDeliveringManager.getPlace(position);
        }

        notifyDataSetChanged();
    }

    /**
     * Get item count. The adapter will iterate over the lines based on this value
     * @return the size of the paths
     */
    @Override
    public int getItemCount() {
        return mPathDeliveringManager.getAllPlaces().size();
    }


}
