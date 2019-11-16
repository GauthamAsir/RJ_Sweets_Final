package agjs.gautham.rjsweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {

    public static String USER_EMAIL = "Email";
    public static String USER_PASS = "Pass";
    public static String USER_Phone = "123";

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

}
