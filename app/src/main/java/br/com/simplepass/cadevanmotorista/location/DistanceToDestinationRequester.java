package br.com.simplepass.cadevanmotorista.location;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain.Van;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import br.com.simplepass.cadevanmotorista.retrofit.MyGoogleApiClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse.Data;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse.GoogleDistanceMatrixResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * DistanceToDestinationRequester is responsible to monitor the time left to arrive at a destination.
 * This class periodicaly consult the Google API to receive the time left to arrive.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class DistanceToDestinationRequester{
    private Van mmVan;
    private Handler mmHandler;
    private Context mmContext;
    private Runnable mRunnable;
    private Place mGoingToPlace;
    private Location mGoingToLocation;
    private boolean isMonitoring;
    private Path mPath;
    public TimeToArriveSender mmTimeToArriveSender;

    public boolean isMonitoring() {
        return isMonitoring;
    }

    private static final int UPDATE_FREQUENCY = 60000; //60 segungos

    /**
     *  Construtor da classe.
     *
     * @param context contexto em que a classe é utilizada
     * @param path Variável representando o caminho que está sendo percorrido, vem como informações como direção...
     * @param van Informações sobre a van. Informações sobre o motorista...
     * @param goingToPlace Próxima localização da rota
     */
    public DistanceToDestinationRequester(Context context, Path path, Van van, Place goingToPlace) {
        mmContext = context;
        mmVan = van;
        HandlerThread mmHandlerThread = new HandlerThread("Map Sync");
        mmHandlerThread.start();
        mmHandler = new Handler(mmHandlerThread.getLooper());
        mPath = path;

        mGoingToPlace = goingToPlace;

        mGoingToLocation = new Location("");
        mGoingToLocation.setLatitude(goingToPlace.getLatitude());
        mGoingToLocation.setLongitude(goingToPlace.getLongitude());

        isMonitoring = false;

        mmTimeToArriveSender = new TimeSender(context, ServiceGenerator.createService(CadeVanMotoristaClient.class));

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Location origin = Locator.getInstance().getLastLocation();

                if(origin != null) {
                    updateWithGoogleAPI(origin, mGoingToLocation, mGoingToPlace);
                }

                mmHandler.postDelayed(this, UPDATE_FREQUENCY);
            }
        };
    }

    /**
     * Set the next going to place
     * @param goingToPlace the next place the van is going
     */
    public void setGoingToPlace(Place goingToPlace){
        mGoingToPlace = goingToPlace;
        mGoingToLocation.setLatitude(goingToPlace.getLatitude());
        mGoingToLocation.setLongitude(goingToPlace.getLongitude());
    }

    /**
     * Starts the monitoring process
     */
    public void startMonitoring(){
        isMonitoring = true;
        mmHandler.post(mRunnable);
    }

    /**
     * Stops the monitoring process
     */
    public void stopMonitoriong(){
        isMonitoring = false;
        mmHandler.removeCallbacks(mRunnable);
        mmVan.setTimeToArrive("");
    }

    /**
     * Restarts the monitoring process
     */
    public void restartMonitoring(){
        mmHandler.removeCallbacks(mRunnable);
        mmHandler.post(mRunnable);
    }

    /**
     * Method responsable for trancking the time to arrive. This method requests Google to know the
     * time to arrive at the next place e evaluates the need to send the time to the student
     * @param origin The origin that Google will calculate the distance. Problably the current position of van
     * @param destination The destination that Google will calculate the distance. Problably the mGoingToPlace
     * @param place The place information. It is importante to evaluate the need to send the time to student
     */
    private void updateWithGoogleAPI(Location origin, final Location destination, final Place place){
        MyGoogleApiClient myGoogleApiClient = ServiceGenerator.createServiceForGoogleAPIs(MyGoogleApiClient.class);
        Call<GoogleDistanceMatrixResponse> callDistanceMatrix =
                myGoogleApiClient.getTimeToArriveToPlace(MyGoogleApiClient.TYPE_OF_ANSWER_JSON,
                        origin.getLatitude() + "," + origin.getLongitude(),
                        destination.getLatitude() + "," + destination.getLongitude(),
                        MyGoogleApiClient.PORTUGUESE_BRAZIL,
                        mmContext.getString(R.string.google_services_key));

        callDistanceMatrix.enqueue(new Callback<GoogleDistanceMatrixResponse>() {
            @Override
            public void onResponse(Call<GoogleDistanceMatrixResponse> call, Response<GoogleDistanceMatrixResponse> response) {
                if(response.isSuccessful() && Locator.getInstance().isDelivering && isMonitoring){
                    Data durationData = response.body()
                            .getRows().get(0)
                            .getElements().get(0)
                            .getDuration();

                    mmVan.setTimeToArrive(durationData.getText());

                    if(durationData.getValue() < 20 * 60) {

                        if (!mmTimeToArriveSender.evaluateIfSent(place,
                                durationData.getValue())) {
                            mmTimeToArriveSender.sendTimeToArrive(mPath,
                                    place,
                                    mPath.getDirection(),
                                    durationData);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GoogleDistanceMatrixResponse> call, Throwable t) {
                Log.d("Retrofit", "Erro: " + t.getMessage());
            }
        });
    }
}
