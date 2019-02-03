package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.MyResponse;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

@Headers(
        {
            "Content-Type:application/json",
            "Authorization:key=AAAAs4Bp9YY:APA91bGH72vJITt6AbNMTTVhp3k0tsGfQq9OZPVFWBg0uh8UyPqT6YXfdLcLOJ7JPN6-5wDs9m6l2xf34LLZ7otoV-2rIfu-cvIfECl8MHg5hEIPQBhmUFrW5rm6siypZ-ISQCQvEBhH"
        }
        )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
