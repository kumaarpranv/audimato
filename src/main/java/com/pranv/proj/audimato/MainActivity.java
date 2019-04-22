package com.pranv.proj.audimato;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends Activity {


    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView txtLabel;
    private TextView socketlbl;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    BroadcastReceiver mReceiver;
    InputStream inStream;
    OutputStream outputStream;
    ConnectedThread mConnectedThread;
    int port;
    DatabaseReference PortRef;
    DatabaseReference Oper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();

        requestRecordAudioPermission();



        /*
         * Registering a new BTBroadcast receiver from the Main Activity context
         * with pairing request event
        */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        PortRef = database.getReference().child("raspberryport");
        Oper=database.getReference().child("raspberryop");
        Log.d("REF",PortRef.toString());

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        txtLabel=findViewById(R.id.txtinputLabel);
        socketlbl=findViewById(R.id.Socketnum);

        Button dev_but = (Button) findViewById(R.id.dev_control);
        dev_but.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DeviceController.class);
                MainActivity.this.startActivity(intent);

            }
        });

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });



    }



    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
    Log.d("PROMPT1","prompted");
        if (!mIslistening)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
        else
        {
            mSpeechRecognizer.stopListening();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            Log.d("BEGIN", "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d("EOS1", "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            Log.d("ERROR1", "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d("onready", "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do



                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("zero",0);
                        map.put("one",1);
                        map.put("two",2);
                        map.put("to",2);
                        map.put("three",3);
                        map.put("0",0);
                        map.put("1",1);
                        map.put("2",2);
                        map.put("3",3);
                        String res=result.get(0);
                        Pattern pattern = Pattern.compile("((.*)(on|off|of)(.*)board (.*))|((.*)board (.*)(on|off|of)(.*))",Pattern.CASE_INSENSITIVE);
                        //
                        String op="";
                        Matcher matcher = pattern.matcher(res);
                        String sock="";
                        if (matcher.find())
                        {


                            String[] dataarr;String[] subd;
                            String text=matcher.group(0);
                            if(text.contains("on")||text.contains("On"))
                                op="on";
                            else
                                op="off";

                            if(text.contains("board")||text.contains("boat")||text.contains("Board")||text.contains("bored")||text.contains("bore")||text.contains("Bored"))
                            {
                                if(text.contains("Boared"))
                                    dataarr = text.split("Boared ", 2);
                                else if(text.contains("boards"))
                                    dataarr = text.split("boards ", 2);
                                else if(text.contains("boat"))
                                    dataarr = text.split("boat ", 2);
                                else if(text.contains("Boards"))
                                    dataarr = text.split("Boards ", 2);
                                else if(text.contains("bored"))
                                    dataarr = text.split("bored ", 2);
                                else if(text.contains("bore"))
                                    dataarr = text.split("bore ", 2);
                                else
                                    dataarr = text.split("board ", 2);
                                if(dataarr[1].contains(" "))
                                {
                                    subd = dataarr[1].split(" ", 2);
                                    System.out.println(subd[0]);
                                    if(map.containsKey(subd[0]))
                                        sock="found board number: "+map.get(subd[0])+"\noperation: "+op;
                                    else
                                        sock="could't find board number!";

                                    port=map.get(subd[0]);
                                }
                                else
                                {
                                    String st=dataarr[1];
                                    if(map.containsKey(st))
                                        sock="found board number: "+map.get(st)+"\noperation: "+op;
                                    else
                                        sock="could't find board number!";

                                    port=map.get(st);
                                }
                            }

                        }



                        DeviceController.init(getBaseContext());
            Set< Map.Entry< Integer,String> > st = DeviceController.triggers.entrySet();
                String pt="";String wd="",cd="",up="";

            for (Map.Entry< Integer,String> me:st)
            {
                wd=me.getValue();
                String ky=me.getKey().toString();
                Log.d("TRIGGERMAP",ky.toString()+" : "+wd);
                pt+="((.*)(on|off|of)(.*)"+wd+"(.*))|((.*)"+wd+"(.*)(on|off|of)(.*))";
                    pt+="|";
            }
            if(!pt.equals("")) {
                pt = pt.substring(0, pt.length() - 1);
                int prt = 0;
                pattern = Pattern.compile(pt, Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(res);
                if (matcher.find()) {
                    String text = matcher.group(0);
                    String dev = "";


                    for (Map.Entry<Integer, String> me : st) {
                        wd = me.getValue();
                        cd = wd.substring(0, 1).toUpperCase() + wd.substring(1).toLowerCase();
                        up=wd.toUpperCase();
                        if (text.contains(wd) || text.contains(cd)||text.contains(up)) {
                            dev = wd;
                            prt = me.getKey();

                            Log.d("PORT1",String.valueOf(prt));
                        }
                    }

                    if (text.contains("on") || text.contains("On"))
                        op = "on";
                    else
                        op = "off";

                    port=prt;
                    sock = "found Device: " + dev + "\noperation: " + op + "\nport: " + prt;
                }



            }


                        txtLabel.setText("Recognized: ");
                        txtSpeechInput.setText(res);
                        socketlbl.setText(sock);
                        PortRef.setValue(port);
                        Oper.setValue(op);




        }



        @Override
        public void onRmsChanged(float rmsdB)

        {
        }




    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }catch(Exception e)
        {
        }

        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;


        }
        //write method
        public void write(String input) {

            byte[] msgBuffer = input.getBytes();            //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }


}