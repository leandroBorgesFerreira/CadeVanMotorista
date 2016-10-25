package br.com.simplepass.cadevanmotorista.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;

/**
 * Created by leandro on 3/29/16.
 */
public class Utils {

    public static void showInfoDialog(Context context, String message){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton("OK", dialogClickListener).show();
    }

    public static String getNameOfPlace(Place place){
        if(place == null){
            throw new IllegalArgumentException("place não pode ser null");
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < place.getEntitysInsidePlace().size(); i++){
            EntityInsidePlace entity = place.getEntitysInsidePlace().get(i);

            if(i == 0){
                stringBuilder.append(entity.getName());
            } else{
                stringBuilder.append(", ").append(entity.getName());
            }
        }

        return stringBuilder.toString();
    }

    public static List<String> getPhonesOfPlaceAsList(Place place){
        if(place == null){
            throw new IllegalArgumentException("place não pode ser null");
        }

        List<String> phoneNumberList = new ArrayList<>();

        for(EntityInsidePlace entity : place.getEntitysInsidePlace()){

            /* Infelizmente aqui deve ser colocado um switch, pois não dá para fazer composição
             * e criar um uma factory, pois esse objeto envolve banco de dados. */
            switch(entity.getType()){
                case EntityInsidePlace.TYPE_STUDENT:
                    phoneNumberList.add(entity.getCountryCode() + entity.getPhone());
                    break;
            }
        }

        return phoneNumberList;
    }

    public static Location getLocationOfPlace(Place place){
        if(place == null){
            throw new IllegalArgumentException("place não pode ser null");
        }

        Location location = new Location("");
        location.setLatitude(place.getLatitude());
        location.setLongitude(place.getLongitude());

        return location;
    }

    public static String addressToString(Address address){
        if(address == null){
            throw new IllegalArgumentException("address não pode ser null");
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < address.getMaxAddressLineIndex(); i++){
            stringBuilder.append(address.getAddressLine(i)).append(" ");
        }

        return stringBuilder.toString();
    }

    public static void circularReveal(View selectable, final View bg, boolean selected){
        if(!selected){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int x = selectable.getLeft() + selectable.getWidth()/2;
                int y = selectable.getTop() + selectable.getHeight()/2;

                float finalRadius = Math.max(bg.getWidth(), bg.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator circularReveal = ViewAnimationUtils.createCircularReveal(bg, x, y, 0, finalRadius);
                circularReveal.setDuration(300);

                // make the view visible and start the animation
                bg.setVisibility(View.VISIBLE);

                circularReveal.start();
            } else {
                bg.setVisibility(View.VISIBLE);
            }
        } else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int x = selectable.getLeft() + selectable.getWidth()/2;
                int y = selectable.getTop() + selectable.getHeight()/2;

                float finalRadius = Math.max(bg.getWidth(), bg.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator circularReveal = ViewAnimationUtils.createCircularReveal(bg, x, y, finalRadius, 0);
                circularReveal.setDuration(300);

                // make the view invisible when the animation is done
                circularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bg.setVisibility(View.INVISIBLE);
                    }
                });

                circularReveal.start();
            } else {
                bg.setVisibility(View.INVISIBLE);
            }
        }

    }
}
