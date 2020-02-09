package agjs.gautham.rjsweets;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import agjs.gautham.rjsweets.Model.AppUpdate;
import agjs.gautham.rjsweets.user.navigation_drawer.settings_user.SettingsActivity;

public class UpdateActivity extends AppCompatActivity {

    private TextView status_version, lastCheck_date, lastCheck_time, update_status, changelog_text, lastCheckText;
    private Button check_update, update;
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference databse_user, database_update;
    private android.app.AlertDialog pgDialog;
    private ProgressBar pg;
    private String updateUrl;
    private Double update_version;
    private String changelog;
    private String app_name;
    private String cureent_version;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        status_version = findViewById(R.id.status_version);
        lastCheck_date = findViewById(R.id.lastCheck_date);
        lastCheck_time = findViewById(R.id.lastCheck_time);
        update_status = findViewById(R.id.update_status);
        changelog_text = findViewById(R.id.textView2);
        lastCheckText = findViewById(R.id.textView);

        check_update = findViewById(R.id.check_update);
        update = findViewById(R.id.update);
        listView = findViewById(R.id.listView);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        databse_user = database.getReference("User");
        database_update = database.getReference("Updates");

        View progressBar = LayoutInflater.from(UpdateActivity.this).inflate(R.layout.progress_bar, null);
        pg = progressBar.findViewById(R.id.progressBar);

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(UpdateActivity.this);
        alertDialog.setTitle("Downloading...");
        alertDialog.setIcon(R.drawable.ic_phone_iphone);
        alertDialog.setView(progressBar);
        alertDialog.setCancelable(true);

        pgDialog = alertDialog.create();
        pgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        cureent_version = getAppVersion(UpdateActivity.this);
        status_version.setText("v"+cureent_version);

        checkForUpdate();

    }

    private void checkForUpdate() {

        Date dt = Calendar.getInstance().getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        final String checkTime = timeFormat.format(dt);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String checkDate = dateFormat.format(dt);

        database_update.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AppUpdate appUpdate = dataSnapshot.getValue(AppUpdate.class);
                updateUrl = appUpdate.getUpdate_url();
                update_version = appUpdate.getVersion();
                changelog = appUpdate.getChangelog();
                app_name = appUpdate.getApp_name();

                cureent_version = getAppVersion(UpdateActivity.this);
                Double app_version = Double.parseDouble(cureent_version);

                if (app_version<update_version){

                    check_update.setVisibility(View.GONE);
                    update.setVisibility(View.VISIBLE);

                    listView.setVisibility(View.VISIBLE);
                    loadChangelog();

                    changelog_text.setVisibility(View.VISIBLE);
                    lastCheckText.setVisibility(View.GONE);
                    lastCheck_date.setVisibility(View.GONE);
                    lastCheck_time.setVisibility(View.GONE);
                    update_status.setText("Update Available v"+update_version);
                    update_status.setVisibility(View.VISIBLE);

                }else {

                    lastCheckText.setVisibility(View.VISIBLE);
                    lastCheck_date.setVisibility(View.VISIBLE);
                    lastCheck_time.setVisibility(View.VISIBLE);

                    changelog_text.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);

                    check_update.setVisibility(View.VISIBLE);
                    update.setVisibility(View.GONE);

                    update_status.setText("Up To Date");
                    update_status.setVisibility(View.VISIBLE);
                    lastCheck_date.setText("Date: " + checkDate);
                    lastCheck_time.setText("Time: " + checkTime);
                    Toast.makeText(UpdateActivity.this,"no Updates",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadChangelog() {

        String[] changelogs = changelog.split(",");

        ArrayAdapter<String> adapter;
        ArrayList<String> arrayList;
        arrayList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listView.setAdapter(adapter);

        for (int i=0; i<changelogs.length; i++){

            arrayList.add(changelogs[i]);
            adapter.notifyDataSetChanged();

        }

    }

    public void bt_check_agian_update(View view) {
        checkForUpdate();
    }

    public void update(View view) {

        pgDialog.show();

        if (ContextCompat.checkSelfPermission(UpdateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

            // execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(UpdateActivity.this,app_name);
            downloadTask.execute(updateUrl);
            Toast.makeText(UpdateActivity.this,"Downloading Update",Toast.LENGTH_SHORT).show();

            pgDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                    pgDialog.dismiss();
                }
            });

        }else {
            ActivityCompat.requestPermissions(UpdateActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            },1);
        }

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private String app_name;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context, String app_name) {
            this.context = context;
            this.app_name = app_name;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(UpdateActivity.this.getExternalFilesDir(null).getAbsolutePath() + "/"+app_name);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pgDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pg.setIndeterminate(false);
            pg.setMax(100);
            pg.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            mWakeLock.release();
            pgDialog.dismiss();
            if (result != null){
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            }
            else{

                /*if (getPackageManager().canRequestPackageInstalls()){

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("content:///"+UpdateActivity.this+"/"+app_name),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else {

                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);

                }*/

                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("content:///"+UpdateActivity.this.getExternalFilesDir(null).getAbsolutePath()+"/"+app_name),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/

                //installPackage(UpdateActivity.this,"agjs.gautham.rjsweets",
                  //      UpdateActivity.this.getFilesDir()+"/"+app_name);

                File file = new File(UpdateActivity.this.getExternalFilesDir(null).getAbsolutePath()
                        + "/" + app_name);
                Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".provider",file);

                Intent installAPK = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                installAPK.setDataAndType(data,"application/vnd.android.package-archive");
                installAPK.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(installAPK);

                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean installPackage(Context context, String packageName, String packagePath){

        PackageManager packageManger = context.getPackageManager();
        PackageInstaller packageInstaller = packageManger.getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(packageName);

        try {
            int sessionId = packageInstaller.createSession(params);
            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
            OutputStream out = session.openWrite(packageName + ".apk", 0, -1);
            session.fsync(out);
            out.close();
            System.out.println("installing...");
            session.commit(PendingIntent.getBroadcast(context, sessionId,
                    new Intent("android.intent.action.MAIN"), 0).getIntentSender());
            System.out.println("install request sent");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getAppVersion(Context context){

        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
