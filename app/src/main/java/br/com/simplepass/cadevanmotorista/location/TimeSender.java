package br.com.simplepass.cadevanmotorista.location;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.dto.PushMessage;
import br.com.simplepass.cadevanmotorista.dto.PushNotification;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse.Data;
import br.com.simplepass.cadevanmotorista.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leandro on 4/6/16.
 */
public class TimeSender implements TimeToArriveSender{
    private Context mContext;
    private Place mLastSentPlace;
    private int mLastSentTime;
    private CadeVanMotoristaClient mServerClient;

    public TimeSender(Context context, CadeVanMotoristaClient client) {
        mContext = context;
        mServerClient = client;
        mLastSentTime = -1;
    }

    //O nome desse método está horrível...
    private int processTime(int time){
        if(time < 20 * 60 && time > 15 * 60){
            return 20 * 60;
        }
        if(time < 15 * 60 && time > 10 * 60){
            return 15 * 60;
        }
        if(time < 10 * 60 && time > 5 * 60){
            return 10 * 60;
        }
        if(time < 5 * 60 && time > 0){
            return 5 * 60;
        }
        if(time == 0){
            return 0;
        }


        return -1;
    }


    @Override
    public void sendArrived(Path path, Place place, String direction) {
        sendTimeToArrive(path, place, direction, new Data(0, ""));
    }

    @Override
    public void sendTimeToArrive(Path path, final Place place, String direction, final Data time) {
        PushNotification pushNotification;

        switch (direction){
            case Path.DIRECTION_SCHOOL:
                if (time.getValue() == 0) {
                    pushNotification = new PushNotification(
                            Utils.getPhonesOfPlaceAsList(place),
                            mContext.getString(R.string.van_arrived),
                            PushMessage.TYPE_ARRIVE_TIME);
                } else {
                    pushNotification = new PushNotification(
                            Utils.getPhonesOfPlaceAsList(place),
                            String.format(mContext.getString(R.string.send_time_to_arrive), time.getText()),
                            PushMessage.TYPE_ARRIVE_TIME);
                }
                break;
            case Path.DIRECTION_HOME:
                List<String> phoneList = new ArrayList<>();

                for(EntityInsidePlace entityToSend : place.getEntitysInsidePlace()){
                    if(entityToSend.getType().equals(EntityInsidePlace.TYPE_SCHOOL)){
                        String schoolName = entityToSend.getName();
                        //Foi descoberto que essa place é um escola

                        //Agora procura todos os alunos desta escola nessa rota
                        for(Place place1 : path.getPlaces()){
                            for(EntityInsidePlace entity : place1.getEntitysInsidePlace()){
                                if(entity.getType().equalsIgnoreCase(EntityInsidePlace.TYPE_STUDENT)
                                        &&
                                    entity.getSchool().equals(schoolName)){

                                    phoneList.add(entity.getCountryCode() + entity.getPhone());;
                                }
                            }
                        }
                    } else if(entityToSend.getType().equals(EntityInsidePlace.TYPE_PARENT)){
                        phoneList.add(entityToSend.getCountryCode() + entityToSend.getPhone());
                    }
                }

                if(phoneList.size() == 0){
                    return;
                }

                if (time.getValue() == 0) {
                    pushNotification = new PushNotification(
                            phoneList,
                            mContext.getString(R.string.van_arrived),
                            PushMessage.TYPE_ARRIVE_TIME);
                } else {
                    pushNotification = new PushNotification(
                            phoneList,
                            String.format(mContext.getString(R.string.send_time_to_arrive), time.getText()),
                            PushMessage.TYPE_ARRIVE_TIME);
                }
                break;
            default:
                return;
        }

        mServerClient = ServiceGenerator.createService(CadeVanMotoristaClient.class);
        Call<Void> callSendTime = mServerClient.sendPushNotification(pushNotification);
        callSendTime.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    mLastSentPlace = place;
                    mLastSentTime = time.getValue();
                } else{
                    Toast.makeText(mContext.getApplicationContext(),
                        "Falha ao enviar aviso de tempo de chegada ao aluno", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(mContext.getApplicationContext(),
                    "Falha ao enviar aviso de tempo de chegada ao aluno", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean evaluateIfSent(Place place, int time) {
        /* Para já ter sido enviado deve ser o mesmo Place, o tempo deve ter sido inicializado
         * e o último tempo deve ser menor ou igual ao tempo que quer ser enviado agora. */
        if(mLastSentPlace == null){
            return false;
        }

        return mLastSentPlace.equals(place) && mLastSentTime != -1 && mLastSentTime < processTime(time);
    }
}
