package agjs.gautham.rjsweets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.Remote.RetroFitClient;
import agjs.gautham.rjsweets.delivery.IGeoCoordinates;

public class Common {

    public static User currentUser = new User();

    public static String USER_EMAIL = "Email";
    public static String USER_PASS = "Pass";
    public static String USER_Phone = "123";
    public static String Name = "123";

    public static String topicName = "News";

    public static String loginType = "";

    public static String IP = "";

    public static String USER_ADDRESS_LINE1= "Address_Line1";
    public static String USER_ADDRESS_LINE2= "Address_Line2";
    public static String USER_ADDRESS_LANDMARK= "Address_Landmark";
    public static String USER_ADDRESS_Pincode= "Address_Pincode";

    public static final String UPDATE = "Update";
    public static final String UPDATE_IMAGE = "Update Image";
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

    public static IGeoCoordinates getGeoCodeService(){
        return RetroFitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight ){
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

}
