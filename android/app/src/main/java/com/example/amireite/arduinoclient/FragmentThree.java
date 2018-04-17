package com.example.amireite.arduinoclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Amireite on 3/29/2018.
 */

public class FragmentThree extends Fragment {
    static String item[] ={"Không hẹn giờ", "60 phút", "30 phút", "1 phút", "5 giây"};
    private boolean swt_flag = false;
    private Socket mSocket;
    {
        try {
            IO.Options options = new IO.Options();

            mSocket = IO.socket("http://192.168.43.109:3484/android");

        } catch (URISyntaxException e) {}
    }
    public FragmentThree() {
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
        return inflater.inflate(R.layout.fragment_three, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSocket.connect();

        final Switch swt1 = (Switch) getView().findViewById(R.id.switch3);
        final Spinner spn1 = (Spinner)getView().findViewById(R.id.spinner_3);
        final SeekBar skBar = (SeekBar) getView().findViewById(R.id.seekBar);
        skBar.setMax(100);
        skBar.setProgress(0);
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
                    j.put("AMOUNT", skBar.getProgress());
                    j.put("TIMER", spn1.getSelectedItemPosition());
                    mSocket.emit("FAN", j);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        skBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                skBar.setProgress(seekBar.getProgress());
                try {
                    JSONObject j = new JSONObject();
                    String value = swt_flag?"ON":"OFF";
                    j.put("POWER", value);
                    j.put("AMOUNT", skBar.getProgress());
                    j.put("TIMER", spn1.getSelectedItemPosition());
                    mSocket.emit("FAN", j);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

    //    mSocket.on("Info", onNewMessage);

    }
    //private Emitter.Listener onNewMessage = new Emitter.Listener() {
    //   @Override
    //    public void call(final Object... args) {
    //        getActivity().runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    //                JSONObject data = (JSONObject) args[0];
    //                String fan;
    //                try {
    //                    fan = data.getString("Quạt");
    //                } catch (JSONException e) {
    //                    return;
    //                }

                    // add the message to view

    //            }
    //        });
    //    }
    //};
}