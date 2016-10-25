package br.com.simplepass.cadevanmotorista.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.EditPlaceActivity;
import br.com.simplepass.cadevanmotorista.activity.PlaceEditor;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmViewHolder;

/**
 * Adapter that show and allow editions in the entities inside a place.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class EntitiesInPlaceAdapter extends RecyclerView.Adapter<EntitiesInPlaceAdapter.ViewHolder> {
    private List<EntityInsidePlace> mEntities;
    private Place mPlace;
    private PlaceEditor mPlaceEditor;

    /**
     *
     * @param place place witch the entities need to be show.
     * @param placeEditor The class responsable to edit the place if necessary
     */
    public EntitiesInPlaceAdapter(Place place, PlaceEditor placeEditor) {
        this.mEntities = place.getEntitysInsidePlace();
        mPlace = place;
        mPlaceEditor = placeEditor;
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RealmViewHolder {
        @Bind(R.id.edit_entity_name) EditText mEntityName;
        @Bind(R.id.edit_entity_phone) EditText mEntityPhone;
        @Bind(R.id.edit_place_address) EditText mEntityAddress;
        @Bind(R.id.edit_place_school) EditText mEntitySchool;
        @Bind(R.id.btn_split) Button mBtnSplit;
        @Bind(R.id.btn_delete) Button mBtnDelete;
        @Bind(R.id.edit_place_phone_container) LinearLayout mPhoneContainer;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View pathView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_entities_in_place, parent, false);

        return new ViewHolder(pathView);
    }

    /**
     * Bind the view and configure the line and the listeners
     * @param holder ViewHolder
     * @param position Current position. This value will vary until the all the lines are iterated.
     */
    @Override
    public void onBindViewHolder(final EntitiesInPlaceAdapter.ViewHolder holder, int position) {
        final EntityInsidePlace entityInsidePlace = mEntities.get(position);

        holder.mEntityName.setText(entityInsidePlace.getName());
        holder.mEntityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mPlaceEditor.getEditTextInFocus(entityInsidePlace,
                            holder.mEntityName,
                            EditPlaceActivity.TYPE_NAME);
                } else{
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    entityInsidePlace.setName(holder.mEntityName.getText().toString());
                    realm.commitTransaction();
                    realm.close();
                }
            }
        });

        holder.mEntityAddress.setText(entityInsidePlace.getAddress());

        if(entityInsidePlace.getType().equals(EntityInsidePlace.TYPE_SCHOOL)){
            holder.mPhoneContainer.setVisibility(View.GONE);
        } else {
            holder.mPhoneContainer.setVisibility(View.VISIBLE);
            holder.mEntityPhone.setText(entityInsidePlace.getPhone());
            holder.mEntityPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mPlaceEditor.getEditTextInFocus(entityInsidePlace,
                                holder.mEntityPhone,
                                EditPlaceActivity.TYPE_PHONE);
                    } else{
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        entityInsidePlace.setPhone(holder.mEntityPhone.getText().toString());
                        realm.commitTransaction();
                        realm.close();
                    }
                }
            });
        }

        holder.mEntitySchool.setText(entityInsidePlace.getSchool());
        holder.mEntitySchool.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPlaceEditor.getEditTextInFocus(entityInsidePlace,
                            holder.mEntitySchool,
                            EditPlaceActivity.TYPE_SCHOOL);
                } else{
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    entityInsidePlace.setSchool(holder.mEntitySchool.getText().toString());
                    realm.commitTransaction();
                    realm.close();
                }
            }
        });

        if(mPlace.getEntitysInsidePlace().size() == 1){
            holder.mBtnSplit.setVisibility(View.GONE);
        }

        holder.mBtnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlaceEditor.splitEntity(mPlace, entityInsidePlace)){
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            }
        });

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteEntity(holder.getAdapterPosition())) {
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            }
        });

        holder.itemView.setTag(entityInsidePlace);
    }

    /**
     * Delete the entity in a position
     *
     * @param position position of the entity to be deleted
     * @return return if the delete was successful
     */
    private boolean deleteEntity(int position){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        if(mPlace.getEntitysInsidePlace().size() == 1){
            mPlace.removeFromRealm();
        } else{
            mPlace.getEntitysInsidePlace().get(position).removeFromRealm();
        }

        realm.commitTransaction();
        realm.close();

        return true;
    }

    /**
     * Get item count. The adapter will iterate over the lines based on this value
     * @return the size of the entities
     */
    @Override
    public int getItemCount() {
        try {
            return mPlace.getEntitysInsidePlace().size();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

}
