package agjs.gautham.rjsweets.admin.navigation_drawer.notification;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import agjs.gautham.rjsweets.Model.MyResponse;
import agjs.gautham.rjsweets.Model.Notification;
import agjs.gautham.rjsweets.Model.Sender;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.common.Common;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification extends Fragment {

    private TextInputLayout title, message;

    private APIService mService;

    private LinearLayout linearLayout;

    AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_notifications_admin, container, false);

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Sending Notification ...")
                .setTheme(R.style.DialogCustom)
                .build();

        title = root.findViewById(R.id.notification_title);
        message = root.findViewById(R.id.notification_message);

        linearLayout = root.findViewById(R.id.notification_container_main);

        mService = Common.getFCMClient();

        FloatingActionButton send = root.findViewById(R.id.notification_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                final String title_text = title.getEditText().getText().toString();
                String message_text = message.getEditText().getText().toString();

                if (validateTitle(title_text) && validateMessage(message_text)){

                    Notification notification = new Notification(message_text, title_text);

                    Sender toTopic = new Sender();

                    toTopic.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                    toTopic.notification = notification;

                    mService.sendNotification(toTopic)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.isSuccessful()){
                                        Snackbar.make(linearLayout,"Message Sent To All Users", Snackbar.LENGTH_LONG).show();
                                        //Toast.makeText(getActivity(),"Message Sent", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        title.getEditText().setText(null);
                                        message.getEditText().setText(null);
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(getActivity(),""+t.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    dialog.dismiss();
                }

            }
        });

        return root;
    }

    boolean validateTitle(String t){

        if (t.isEmpty()){

            dialog.dismiss();
            title.setError("Field Can't be Empty");
            title.requestFocus();
            return false;
        }else {
            title.clearFocus();
            title.setError(null);
            return true;
        }

    }

    boolean validateMessage(String msg){

        if (msg.isEmpty()){

            dialog.dismiss();
            message.setError("Field Can't be Empty");
            message.requestFocus();
            return false;
        }else {
            message.setError(null);
            message.clearFocus();
            return true;
        }

    }
}