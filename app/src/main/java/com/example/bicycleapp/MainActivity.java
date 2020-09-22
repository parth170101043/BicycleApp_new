package com.example.bicycleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
   // Button save;
    DatabaseReference reff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect();
    }
    public void connect()
    {//pahoMqttClient = new PahoMqttClient();
        reff= FirebaseDatabase.getInstance().getReference().child("Cycle");
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://soldier.cloudmqtt.com:10511",
                        clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("wewyhfbx");
        options.setPassword("Ff3UseLPny3p".toCharArray());


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    // Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    publish(client);

                    subscribe(client,"web");
                    subscribe(client,"file");
                    client.setCallback(new MqttCallback() {
                        TextView aa = (TextView) findViewById(R.id.aa);
                      //  TextView bb = (TextView) findViewById(R.id.bb);
                        @Override
                        public void connectionLost(Throwable cause) {

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.d("file", message.toString());


                            if (topic.equals("file")){
                                Member member=new Member();
                                aa.setText(message.toString());
                                member.setName(message.toString());
                               // reff.child("Cycle").setValue(member);
                                reff.push().setValue(member);
                               // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            }

                        }
                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    //Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "disconneted", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void publish(MqttAndroidClient client)
    {
        String topic = "foo/bar";
        String payload = "the payload";
        //byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(2);
            message.setRetained(false);
            client.publish(topic, message);
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }
    public void subscribe(MqttAndroidClient client,String topic )
    {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void hist(View view)
    {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }
    public void mapp(View view)
    {
        Intent intent = new Intent(this, cycle_Map.class);
        startActivity(intent);
    }
}
