package com.example.amireite.arduinoclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Amireite on 3/29/2018.
 */


public class FragmentOne extends Fragment {
    static String item[] ={"Không hẹn giờ", "60 phút", "30 phút", "1 phút", "5 giây"};
    private boolean swt_flag = false;
    private Socket mSocket;
    {
        try {
            IO.Options options = new IO.Options();

            mSocket = IO.socket("http://192.168.43.109:3484/android");

        } catch (URISyntaxException e) {}
    }
    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Switch swt1 = (Switch) getView().findViewById(R.id.switch1);
        final Spinner spn1 = (Spinner)getView().findViewById(R.id.spinner_1);
        ArrayAdapter<String> Array= new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, item);
        Array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn1.setAdapter(Array);
        swt1.setChecked(swt_flag);
        swt1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swt_flag = swt_flag ? false : true;
                swt1.setChecked(swt_flag);
                try {
                    JSONObject j = new JSONObject();
                    String value = swt_flag?"ON":"OFF";
                    j.put("POWER", value);
                    j.put("TIMER", spn1.getSelectedItemPosition());
                    mSocket.emit("LED 1", j);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }
}