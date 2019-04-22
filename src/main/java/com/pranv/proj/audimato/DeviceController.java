package com.pranv.proj.audimato;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceController extends Activity {
    private ListView mListview;
    TextView v0,v1,v2,v3;
    Button but0,but1,but2,but3;
    public static Map<Integer,String> triggers;
    String fileName="devicedb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        getActionBar().hide();
       // MyProperties.getInstance().triggers.add("fan");MyProperties.getInstance().triggers.add("light");
       // MyProperties.getInstance().triggerind.add(0);MyProperties.getInstance().triggerind.add(1);

        Button dev_but = (Button) findViewById(R.id.speech);
        dev_but.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(DeviceController.this, MainActivity.class);
                DeviceController.this.startActivity(intent);

            }
        });
        init(this.getBaseContext());

        v0=findViewById(R.id.edit0);
        v1=findViewById(R.id.edit1);
        v2=findViewById(R.id.edit2);
        v3=findViewById(R.id.edit3);

        Log.d("triggers",triggers.get(0));
        if(triggers.get(0)!=null)
        v0.setText(triggers.get(0));
        if(triggers.get(1)!=null)
            v1.setText(triggers.get(1));
        if(triggers.get(2)!=null)
            v2.setText(triggers.get(2));
        if(triggers.get(3)!=null)
            v3.setText(triggers.get(3));





        but0 = (Button) findViewById(R.id.but0);
        but0.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v0= findViewById(R.id.edit0);
                triggers.put(0,v0.getText().toString());

                Toast.makeText(DeviceController.this.getBaseContext(),v0.getText()+" setted to 0",Toast.LENGTH_LONG).show();

                DeviceController.writeTXT(getBaseContext());
            }
        });

        but1 = (Button) findViewById(R.id.but1);
        but1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v1= findViewById(R.id.edit1);
                triggers.put(1,v1.getText().toString());
                Toast.makeText(DeviceController.this.getBaseContext(),v1.getText()+" setted to 1",Toast.LENGTH_LONG).show();

                writeTXT(getBaseContext());
            }
        });

        but2 = (Button) findViewById(R.id.but2);
        but2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v2= findViewById(R.id.edit2);
                triggers.put(2,v2.getText().toString());
                Toast.makeText(DeviceController.this.getBaseContext(),v2.getText()+" setted to 2",Toast.LENGTH_LONG).show();
                writeTXT(getBaseContext());
            }
        });

        but3 = (Button) findViewById(R.id.but3);
        but3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v3= findViewById(R.id.edit3);
                triggers.put(3,v3.getText().toString());

                Toast.makeText(DeviceController.this.getBaseContext(),v3.getText()+" setted to 3",Toast.LENGTH_LONG).show();
                writeTXT(getBaseContext());
            }
        });

       // fd.writeTXT();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }



        static void init(Context context)
        {


            triggers=new HashMap<Integer, String>();
            SharedPreferences mshared = PreferenceManager.getDefaultSharedPreferences(context);
            if(mshared.contains("0")&&mshared.contains("1")&&mshared.contains("2")&&mshared.contains("3"))
                readTXT(context);
            else
                initTXT(context);



        }

        static void initTXT(Context context)
        {
            SharedPreferences mshared = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor mEditor=mshared.edit();
            for(int i=0;i<4;i++)
            {
                mEditor.putString(Integer.toString(i),"device "+Integer.toString(i));
                mEditor.commit();
            }

        }

        static void readTXT(Context context)
        {

            SharedPreferences mshared = PreferenceManager.getDefaultSharedPreferences(context);

            for(int i=0;i<4;i++)
            {

                String device=mshared.getString(Integer.toString(i),"default");
                Log.d("READSHARED",device);

                triggers.put(i,device);
            }
        }

        static void writeTXT(Context context)
        {
           SharedPreferences mshared = PreferenceManager.getDefaultSharedPreferences(context);
           SharedPreferences.Editor mEditor=mshared.edit();
            for(int i=0;i<4;i++)
            {
                mEditor.putString(Integer.toString(i),triggers.get(i));
                mEditor.commit();
            }
        }

/*    public static  void main(String[] args)
    {
        FileDB fd=new FileDB();
        System.out.println(triggers.get(0));
    }
*/

    }

