package com.example.bicycleapp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class cycle_Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle__map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void connect()
    {//pahoMqttClient = new PahoMqttClient();
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
                    Toast.makeText(cycle_Map.this, " yes", Toast.LENGTH_SHORT).show();
                    publish(client);

                    subscribe(client,"web");
                    //   subscribe(client,"Partha");
                    client.setCallback(new MqttCallback() {
                        // TextView aa = (TextView) findViewById(R.id.aa);
                        // TextView bb = (TextView) findViewById(R.id.bb);
                        @Override
                        public void connectionLost(Throwable cause) {

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            // Log.d("file", message.toString());

                            if (topic.equals("web")){
                                String str =message.toString();
                                String[] arrOfStr =str.split(",");
                                String aa=arrOfStr[0];
                                String bb=arrOfStr[1];
                                double a=Double.parseDouble(aa);
                                double b=Double.parseDouble(bb);
                                // Add a marker in Sydney and move the camera
                                LatLng sydney = new LatLng(a,b);
                                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
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
                    Toast.makeText(cycle_Map.this, " no", Toast.LENGTH_SHORT).show();

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(26, 91);
        mMap.addMarker(new MarkerOptions().position(sydney).title("bicycle"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,7));

        connect();
    }
}
