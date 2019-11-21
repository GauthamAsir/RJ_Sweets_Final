package agjs.gautham.rjsweets.Remote;

import agjs.gautham.rjsweets.Model.MyResponse;
import agjs.gautham.rjsweets.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {

    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization:key=AAAAMJYdBkY:APA91bFGVaaI4x7mXIi_I36oYp-x0Spf3Zo9y_sz0sUfWCI0p5UpfDppRQUsVx6SR6Fp728xby3PMwZtjru9KIpUr-a8r-3ti2SOZD4JwK1XbXQs6ZA0jj-rFYlDHq6tgJAuwKNe-1Td"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
