package agjs.gautham.rjsweets;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Helper.GMailSender;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.Remote.RetroFitClient;

public class Common {

    public static User currentUser = new User();

    public static String USER_EMAIL = "Email";
    public static String USER_PASS = "Pass";
    public static String USER_Phone = "123";
    public static String Name = "123";

    public static String topicName = "News";

    private static String sender_mail = "agjs.bros@gmail.com";
    private static String password = "jn@123456789";
    private static String body;

    public static String loginType = "";

    public static String IP = "";

    public static String USER_ADDRESS_SAVED= "Saved Address Temp";

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static String PHONE_KEY = "Phone";

    public static List<SweetOrder> list = new ArrayList<>();

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static final String fcmUrl = "https://fcm.googleapis.com/";

    public static final String baseUrl = "https://maps.googleapis.com";

    public static APIService getFCMClient(){
        return FCMRetroFitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null){
                if (info.getType() == ConnectivityManager.TYPE_WIFI){
                    return true;
                }else return info.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    public static APIService getFCMService(){
        return RetroFitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "On the Way";
            case "2":
                return "Delivered";
            case "3":
                return "Rejected";
            default:
                return "Rejected Default";
        }
    }

    public static void sendMail(final String mail, final String order_id, final String user_name,
                                final String o_date, final String o_total, final String o_phone,
                                final String status, final String deliveredBy){

        body = "<html><head><style>";

        body += "h1{background-color: green;} p {background-color: #FFFFFF);}.boxed {height: 350px; padding: 50px; border: 3px solid green;}";
        body += "<style> { top:0px; padding-top:0; margin:auto; position:relative; width:1280 px; height:100%;}</style>";
        body += "</style></head><body>";

        body = "<h1><center><img src=\"https://firebasestorage.googleapis.com/v0/b/agjsproject.appspot.com/o/%20images%2Fmain_logo.png?alt=media&token=f66bbec2-3db5-416e-9c2e-2ffd6202ca36\" height=\"75\" width=\"200\"></center></h1>";
        body += String.format("<span style=\"float: left\">Hii <b> %s,</b> <br> Your order %s </span><br>",user_name, statusConvert(status));
        body += String.format("<br><span style=\"float: left\">Order Placed on <b> %s </b> <br></span><br>",o_date);

        body += "<br><br>";
        body += String.format("Order Number <b>%s</b>",order_id);

        String img = null;

        if (status.equals("0"))
            img = "https://firebasestorage.googleapis.com/v0/b/agjsproject.appspot.com/o/%20images%2Fplaced.png?alt=media&token=4106ef10-6bc5-4d9d-a2ec-ce401eeba03b";
        else if (status.equals("1"))
            img = "https://firebasestorage.googleapis.com/v0/b/agjsproject.appspot.com/o/%20images%2Fon_the_way.png?alt=media&token=b2479a9d-bb7a-4879-927e-1a2f56f0823b";
        else if (status.equals("2"))
            img = "https://firebasestorage.googleapis.com/v0/b/agjsproject.appspot.com/o/%20images%2Fdelivered.png?alt=media&token=feb52bb2-29d7-47b4-9a43-9dfaa63726aa";

        body += "<br><br>";
        body += String.format("<div class=\"boxed\"><span style=\"float: left\"><img src=\"%s\" height=\"75\" width=\"500\" style=\"vertical-align:top\">",img);

        body += "<br><br><br><br>";

        if (status.equals("0"))
            body += String.format("<font size=\"3\">Amount Payable <b> %sRs</b></font></span><br>", o_total);
        else if (status.equals("1"))
            body += String.format("<font size=\"3\">Picked By <b> %s</b></font></span><br>", deliveredBy);
        else if (status.equals("2"))
            body += String.format("<font size=\"3\">Delivered By <b> %s</b></font></span><br>", deliveredBy);
        else if (status.equals("3"))
            body += String.format("<font size=\"3\">Rejected By <b> %s</b></font></span><br>", deliveredBy);

        body += String.format("<br><font size=\"3\"><b>Order Updates Sent To</b><br>%s</font>",o_phone);

        body += "<br> <br> <br> <br> <br> <br> <br> <br> <br> <br> <br> <br> <br> ";
        body += "</body></html>";

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(sender_mail, password);
                    sender.sendMail("Order No. " + order_id + " " + statusConvert(status),
                            body,
                            sender_mail,
                            mail);

                } catch (Exception e) {
                    Log.e("Mail Error", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

    private static String statusConvert(String o_status){

        switch (o_status) {
            case "0":
                return "is Placed";
            case "1":
                return "is On the Way";
            case "2":
                return "has been Delivered";
            case "3":
                return "is Rejected";
            default:
                return "is Rejected by Default";
        }

    }

    public static void intentOpenAnimation(Activity activity){
        activity.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
    }

    public static void intentCloseAnimation(Activity activity){
        activity.overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
    }

}
