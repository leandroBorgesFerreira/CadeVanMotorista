package br.com.simplepass.cadevanmotorista.location;

import android.content.Context;
import android.widget.Toast;

import br.com.simplepass.cadevanmotorista.domain.Van;
import br.com.simplepass.cadevanmotorista.retrofit.AccessToken;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.retrofit.responses.DefaultResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Classe destinada ao envio das informações da van para o servidor
 */
public class LocationSender {
    /**
     * Mótodo para envio das informações da van para o servidor
     * @param van Van que será enviada ao servidor
     */
    public static void sendLocation(Van van){
        CadeVanMotoristaClient clientService = ServiceGenerator.createService(
                CadeVanMotoristaClient.class);

        Call<Void> callLocation = clientService.sendLocation(van);
        callLocation.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    /*Toast.makeText(context.getApplicationContext(), "Falha ao enviar localização",
                            Toast.LENGTH_SHORT).show();*/
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                /*Toast.makeText(context.getApplicationContext(), "Falha ao enviar localização",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

    }

}
