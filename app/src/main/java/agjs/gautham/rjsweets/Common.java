package agjs.gautham.rjsweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.Remote.RetroFitClient;

public class Common {

    public static User currentUser;

    public static String USER_EMAIL = "Email";
    public static String USER_PASS = "Pass";
    public static String USER_Phone = "123";
    public static String Name = "123";

    public static String USER_ADDRESS_LINE1= "Address_Line1";
    public static String USER_ADDRESS_LINE2= "Address_Line2";
    public static String USER_ADDRESS_LANDMARK= "Address_Landmark";
    public static String USER_ADDRESS_Pincode= "Address_Pincode";

    public static List<SweetOrder> list = new ArrayList<>();

    private static final String BASE_URL = "https://fcm.googleapis.com/";

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

}
