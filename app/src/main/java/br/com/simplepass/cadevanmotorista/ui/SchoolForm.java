package br.com.simplepass.cadevanmotorista.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.ProgressShower;
import br.com.simplepass.cadevanmotorista.dto.PlaceFormResult;
import br.com.simplepass.cadevanmotorista.dto.SchoolFormResult;
import br.com.simplepass.cadevanmotorista.location.Locator;

/**
 * Created by leandro on 3/28/16.
 */
public class SchoolForm implements PlaceForm {
    private Context mContext;
    private ProgressShower mProgressShower;
    private ViewGroup mInsideContainer;
    private ViewGroup mOutsideContainer;
    private AutoCompleteTextView mNameAutoComplete;
    private EditText mAddressEditText;

    public final static int ANIMATION_DURATION  = 300;

    public SchoolForm(Activity activity, ViewGroup container, ProgressShower progressShower) {
        mOutsideContainer = container;

        mContext = activity;
        mProgressShower = progressShower;

        View layout = activity.getLayoutInflater().inflate(R.layout.form_add_school, container);

        mInsideContainer = (LinearLayout) layout.findViewById(R.id.form_add_path);

        mNameAutoComplete = (AutoCompleteTextView)layout.findViewById(R.id.form_place_name);
        String[] schools = mContext.getResources().getStringArray(R.array.list_of_schools);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, schools);
        mNameAutoComplete.setAdapter(adapter);

        mAddressEditText = (EditText)layout.findViewById(R.id.edit_place_address);

        ImageButton myPositionButton = (ImageButton) layout.findViewById(R.id.form_place_my_location);
        myPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordToAddressTask coordToAddressTask = new CoordToAddressTask(mContext,
                        mProgressShower,
                        Locator.getInstance().getLastLocation(),
                        mAddressEditText);

                mProgressShower.showProgress(true);
                coordToAddressTask.execute();
            }
        });
    }

    @Override
    public void insertForm() {
        for (int i = 0; i < mInsideContainer.getChildCount(); i++) {
            View v = mInsideContainer.getChildAt(i);
            ViewPropertyAnimator animator = v.animate()
                    .scaleX(1).scaleY(1)
                    .setDuration(ANIMATION_DURATION);

            animator.setStartDelay(ANIMATION_DURATION);
            animator.start();
        }
    }

    @Override
    public void removeForm() {
        /*for (int i = 0; i < mInsideContainer.getChildCount(); i++) {
            View v = mInsideContainer.getChildAt(i);
            ViewPropertyAnimator animator = v.animate()
                    .scaleX(0).scaleY(0)
                    .setDuration(ANIMATION_DURATION);

            animator.setStartDelay(ANIMATION_DURATION);
            animator.start();
        }*/

        mOutsideContainer.removeAllViews();
    }

    @Override
    public PlaceFormResult getPlace() {
        SchoolFormResult place = new SchoolFormResult();

        place.setName(mNameAutoComplete.getText().toString());
        place.setAddress(mAddressEditText.getText().toString());

        return place;
    }

    @Override
    public boolean isCorrect() {
        mNameAutoComplete.setError(null);
        mAddressEditText.setError(null);

        String name = mNameAutoComplete.getText().toString();
        String address = mAddressEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(address)){
            cancel = true;
            focusView = mAddressEditText;
            mAddressEditText.setError(mContext.getString(R.string.error_field_required));
        }

        if(TextUtils.isEmpty(name)){
            cancel = true;
            focusView = mNameAutoComplete;
            mNameAutoComplete.setError(mContext.getString(R.string.error_field_required));
        }

        if(cancel){
            focusView.requestFocus();
            return false;
        } else{
            return true;
        }
    }
}
