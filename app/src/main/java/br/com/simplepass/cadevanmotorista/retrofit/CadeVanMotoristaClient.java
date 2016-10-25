package br.com.simplepass.cadevanmotorista.retrofit;

import java.util.List;

import br.com.simplepass.cadevanmotorista.domain.Driver;
import br.com.simplepass.cadevanmotorista.domain.Van;
import br.com.simplepass.cadevanmotorista.dto.PushNotification;
import br.com.simplepass.cadevanmotorista.dto.RecoverPasswordBean;
import br.com.simplepass.cadevanmotorista.retrofit.responses.OAuthTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by leandro on 3/7/16.
 */
public interface CadeVanMotoristaClient {
    @POST("drivers")
    Call<Driver> register(@Body Driver driver);

    @GET("drivers")
    Call<List<Driver>> getDriversByPhoneNumber(@Query("phoneNumber") String phone);

    @POST("oauth/token")
    @FormUrlEncoded
    Call<OAuthTokenResponse> getAuthToken(@Field("username") String username,
                                          @Field("password") String password,
                                          @Field("grant_type") String grantType);

    @POST("messages")
    Call<Void> sendPushNotification(@Body PushNotification pushNotification);

    @POST("vans")
    Call<Void> sendLocation(@Body Van van);

    @POST("drivers/recoverPassword")
    Call<Driver> recoverPassword(@Body RecoverPasswordBean recoverPasswordBean);

    @PUT("drivers/{id}")
    Call<Void> updateUDriver(@Body Driver driver, @Path("id") long id);

}
