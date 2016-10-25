package br.com.simplepass.cadevanmotorista.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.ProgressShower;
import br.com.simplepass.cadevanmotorista.dto.PlaceFormResult;
import br.com.simplepass.cadevanmotorista.dto.StudentFormResult;
import br.com.simplepass.cadevanmotorista.location.Locator;
import br.com.simplepass.cadevanmotorista.utils.FormUtils;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by leandro on 3/28/16.
 */
public class StudentForm implements PlaceForm {
    private Activity mActivity;
    private ProgressShower mProgressShower;
    private ViewGroup mInsideContainer;
    private ViewGroup mOutsideContainer;
    private AutoCompleteTextView mNameAutoComplete;
    private EditText mPhoneEditText;
    private EditText mCountryPhoneEditText;
    private EditText mAddressEditText;
    private AutoCompleteTextView mSchoolNameAutoComplete;

    private static final String NAME = "Name";
    private static final String PHONE = "Phone";

    public final static int ANIMATION_DURATION  = 300;

    //ToDo: Tentar colocar o butterknife aqui
    public StudentForm(Activity activity, ViewGroup container, ProgressShower progressShower) {
        mOutsideContainer = container;

        mActivity = activity;
        mProgressShower = progressShower;

        View layout = activity.getLayoutInflater().inflate(R.layout.form_add_student, container);

        mInsideContainer = (LinearLayout) layout.findViewById(R.id.form_add_path);

        mNameAutoComplete = (AutoCompleteTextView) layout.findViewById(R.id.form_place_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(mActivity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                FillContactsTask fillContactsTask = new FillContactsTask(mNameAutoComplete);
                fillContactsTask.execute();
            }
        } else {
            FillContactsTask fillContactsTask = new FillContactsTask(mNameAutoComplete);
            fillContactsTask.execute();
        }

        mNameAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = (Map<String, String>) parent.getItemAtPosition(position);

                mNameAutoComplete.setText(map.get(NAME));
                mPhoneEditText.setText(map.get(PHONE));

                //A view que pede focus deveria ser relativa, não estatica dessa maneira.
                mAddressEditText.requestFocus();
            }
        });

        mCountryPhoneEditText = (EditText)layout.findViewById(R.id.form_place_country_phone);
        mPhoneEditText = (EditText)layout.findViewById(R.id.form_place_phone);
        mPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPhoneEditText.addTextChangedListener(FormUtils.areaCodeFixer());

        mAddressEditText = (EditText)layout.findViewById(R.id.form_place_address);

        mSchoolNameAutoComplete = (AutoCompleteTextView) layout.findViewById(R.id.form_place_school_name);
        String[] schools = mActivity.getResources().getStringArray(R.array.list_of_schools);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, schools);
        mSchoolNameAutoComplete.setAdapter(adapter);

        ImageButton myPositionButton = (ImageButton) layout.findViewById(R.id.form_place_my_location);
        myPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordToAddressTask coordToAddressTask = new CoordToAddressTask(mActivity,
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
        StudentFormResult formResult = new StudentFormResult();

        formResult.setName(mNameAutoComplete.getText().toString());
        formResult.setCountryCode(mCountryPhoneEditText.getText().toString().replaceAll("\\D", ""));
        formResult.setPhone(mPhoneEditText.getText().toString().replaceAll("\\D", ""));
        formResult.setAddress(mAddressEditText.getText().toString());
        formResult.setSchoolName(mSchoolNameAutoComplete.getText().toString());

        return formResult;
    }

    @Override
    public boolean isCorrect() {
        mNameAutoComplete.setError(null);
        mAddressEditText.setError(null);
        mSchoolNameAutoComplete.setError(null);

        String name = mNameAutoComplete.getText().toString();
        String address = mAddressEditText.getText().toString();
        String schoolName = mSchoolNameAutoComplete.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(schoolName)){
            cancel = true;
            focusView = mSchoolNameAutoComplete;
            mSchoolNameAutoComplete.setError(mActivity.getString(R.string.error_field_required));
        }

        if(TextUtils.isEmpty(address)){
            cancel = true;
            focusView = mAddressEditText;
            mAddressEditText.setError(mActivity.getString(R.string.error_field_required));
        }

        if(TextUtils.isEmpty(name)){
            cancel = true;
            focusView = mNameAutoComplete;
            mNameAutoComplete.setError(mActivity.getString(R.string.error_field_required));
        }

        if(cancel){
            focusView.requestFocus();
            return false;
        } else{
            return true;
        }
    }

    private class FillContactsTask extends AsyncTask<Void, Void, List<Map<String, String>>>{
        private AutoCompleteTextView autoCompleteTextView;

        public FillContactsTask(AutoCompleteTextView autoCompleteTextView) {
            this.autoCompleteTextView = autoCompleteTextView;
        }

        private List<Map<String, String>> getContacts(){
            ArrayList<Map<String, String>> namePhoneList = new ArrayList<>();

            Cursor people = mActivity.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            while (people.moveToNext()) {
                String contactName = people.getString(people
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = people.getString(people
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String hasPhone = people.getString(people
                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if ((Integer.parseInt(hasPhone) > 0)){
                    Cursor phones = mActivity.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                            null,
                            null);

                    while (phones.moveToNext()){
                        String phoneNumber = phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Map<String, String> namePhone = new HashMap<>();
                        namePhone.put(NAME, contactName);
                        namePhone.put(PHONE, phoneNumber);

                        namePhoneList.add(namePhone);
                    }

                    phones.close();
                }
            }

            people.close();

            return namePhoneList;
        }

        @Override
        protected List<Map<String, String>> doInBackground(Void... params) {
            return getContacts();
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> maps) {
            SimpleAdapter simpleAdapter = new SimpleAdapter(mActivity,
                    maps,
                    R.layout.item_list_contact,
                    new String[]{NAME, PHONE},
                    new int[]{R.id.accountName, R.id.accountPhone});

            autoCompleteTextView.setAdapter(simpleAdapter);
        }
    }
}
