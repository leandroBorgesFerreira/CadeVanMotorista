package br.com.simplepass.cadevanmotorista.retrofit;

import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse.GoogleDistanceMatrixResponse;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse.GoogleGeocodeResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by leandro on 3/22/16.
 */
public interface MyGoogleApiClient {
    @GET("maps/api/distancematrix/{typeOfAnswer}")
    Call<GoogleDistanceMatrixResponse> getTimeToArriveToPlace(
            @Path("typeOfAnswer") String typeOfAnswer,
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("language") String language,
            @Query("key") String key);

    @GET("maps/api/geocode/{typeOfAnswer}")
    Call<GoogleGeocodeResponse> getPositionFromAddress(
            @Path("typeOfAnswer") String typeOfAnswer,
            @Query("address") String address,
            @Query("language") String language,
            @Query("key") String key);

    String TYPE_OF_ANSWER_JSON = "json";
    String PORTUGUESE_BRAZIL = "pt-BR";
}
