package com.example.tanushajayasinghe.cnc_demo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import it.beppi.knoblibrary.Knob;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    MQTTHelperPublisher mqttHelperPublisher;
    TextView editText;
    Button btn;
    Knob knob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMqtt();
        mqttHelperPublisher = new MQTTHelperPublisher(getApplicationContext());
        editText = findViewById(R.id.textView10);
//        btn = findViewById(R.id.button);
//        btn.setOnCl/ickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mqttHelper.sendMessage(editText.getText().toString());
//            }
//        });
        knob  = (Knob) findViewById(R.id.knob);
        knob.setState(0);
        knob.setKnobCenterColor(Color.BLUE);
//        knob.bal
        knob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
//                MqttMessage message = new MqttMessage();
//                message.setPayload(Byte.decode(String.valueOf(state)));
//                Log.d("URI", mqttHelperPublisher.mqttAndroidClient.get)
                mqttHelperPublisher.sendMessage(String.valueOf(state));

//                Toast.makeText(getApplicationContext(), String.valueOf(state), Toast.LENGTH_LONG).show();


            }
        });

    }

    private void startMqtt(){
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(getApplicationContext(), "Connected to the server", Toast.LENGTH_LONG).show();

            }

            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "lost", Toast.LENGTH_LONG).show();

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//                Toast.makeText(getApplicationContext(), "mess", Toast.LENGTH_LONG).show();
                try {
                    Log.w("Debug", mqttMessage.toString().replace("/r","").replace("/n",""));
                    JSONObject object = new JSONObject(mqttMessage.toString().replace("/r","").replace("/n",""));
                    String status = object.get("state").toString().toLowerCase();
                    if (status.equals("idle")) {
                        knob.setKnobCenterColor(Color.GREEN);
                    } else if (status.equals("run")) {
                        knob.setKnobCenterColor(Color.RED);
                    } else {
                        knob.setKnobCenterColor(Color.BLACK);
                    }
                    String num = object.get("value").toString().split("\r")[0];
                    editText.setText(num);
                }catch(JSONException e){
                    e.printStackTrace();
                }
//                dataReceived.setText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
