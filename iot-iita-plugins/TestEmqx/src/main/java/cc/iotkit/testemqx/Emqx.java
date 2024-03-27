package cc.iotkit.testemqx;


import org.eclipse.paho.client.mqttv3.MqttClient;

public class Emqx implements Mqtt{

    private MqttClient mqttClient;

    public Emqx(MqttClient mqttClient){
        this.mqttClient=mqttClient;
    }
    @Override
    public void subscribe(String topic) {

    }

    @Override
    public void publish(String msg) {

    }
}
