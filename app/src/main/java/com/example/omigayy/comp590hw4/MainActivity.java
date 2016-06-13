package com.example.omigayy.comp590hw4;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    Bean b;
    String action;
    String pos;
    boolean print = false;
    Integer fileNum = 1;
    ArrayList<String> rawList = new ArrayList<String>();
    ArrayList<String> dataList = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeanManager.getInstance().startDiscovery(bdl);

    }

    BeanDiscoveryListener bdl = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            Log.v("TT", "" + bean.getDevice() + ", " + rssi);
            b = bean;
        }

        @Override
        public void onDiscoveryComplete() {
            b.connect(getApplicationContext(), blsnr);
        }
    };

    BeanListener blsnr = new BeanListener() {
        @Override
        public void onConnected() {

            Log.v("TT", "We are connected to: " + b.getDevice().getName());

            b.readAcceleration(new Callback<Acceleration>() {
                @Override
                public void onResult(Acceleration result) {
                    Log.v("TT", "" + result.x() + ", " + result.y() + ", " + result.z());
                }
            });
        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onSerialMessageReceived(byte[] data) {
            if(print==true) {

                String value = null;
                int tmpV;
                boolean dollar = false;
                try {
                    value = new String(data,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try{
                    tmpV = Integer.parseInt(value);
                }catch(Exception e){
                    dollar = true;
                }

                if(dollar){
                    Calendar c = Calendar.getInstance();
                    long now = c.getTimeInMillis();
                    System.out.println(now);
                    rawList.add("\n" + Long.toString(now));
                }
                else {
                    System.out.println(value);
                    rawList.add(" "+value);
                }
            }
        }

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value) {

        }

        @Override
        public void onError(BeanError error) {

        }
    };


    public void onRadioButtonClicked1(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.Standing:
                if (checked)
                    action = "STANDING";
                    break;
            case R.id.Sitting:
                if (checked)
                    action = "SITTING";
                    break;
            case R.id.Walking:
                if (checked)
                    action = "WALKING";
                    break;
            case R.id.Running:
                if (checked)
                    action = "RUNNING";
                    break;
            case R.id.Upstairs:
                if (checked)
                    action = "UPSTAIRS";
                    break;
            case R.id.Downstairs:
                if (checked)
                    action = "DOWNSTAIRS";
                    break;
        }
    }

    public void onRadioButtonClicked2(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.WAIST:
                if (checked)
                    pos = "WAIST";
                    break;
            case R.id.WRIST:
                if (checked)
                    pos = "WRIST";
                    break;
            case R.id.SHOE:
                if (checked)
                    pos = "SHOE";
                    break;
        }
    }

    public void clickStart(View v){

        rawList.clear();;
        print = true;

    }

    public void clickStop(View v){
        print = false;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("E", "Directory not created");
        }
        return file;
    }

    public void clickSave(View v){

        String tmp= "";
        int index = 0;
        dataList.clear();
        System.out.println("=========================");

        for (int i = 0;i<rawList.size();i++){
            System.out.println(rawList.get(i));
//            System.out.println(rawList.get(i+1));
//            System.out.println(rawList.get(i+2));
        }


        String fileName = action + "_" + pos + "_" + fileNum;


//        File file = getAlbumStorageDir("testbiubiu");
//
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/comp590");
        myDir.mkdirs();

        File file = new File (myDir, fileName);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);

            for(int i = 0;i<rawList.size();i++) {
                byte[] contentInBytes = rawList.get(i).getBytes();
                out.write(contentInBytes);
                out.flush();
            }
            out.close();
            fileNum++;
        } catch (Exception e) {
            e.printStackTrace();
        }

//        FileOutputStream outputStream;
////
//        try {
////            File file = new File(getApplicationContext().getFilesDir(), fileName);
//
//
//            outputStream = openFileOutput(fileName, Context.MODE_WORLD_READABLE);
//            fileNum++;
//            outputStream.write("test".getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }



}

