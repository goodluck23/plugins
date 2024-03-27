package cc.iotkit.testemqx.config;


import cc.iotkit.testemqx.Emqx;
import cc.iotkit.testemqx.Mqtt;
import cc.iotkit.testemqx.OnMessageCallback;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Configurable
public class EmqxConfig {
    @Value("${emqx.host}")
    private String host;
    @Value("${emqx.port}")
    private String port;
    @Value("${emqx.clientId}")
    private String clientId;

    @Bean
    public Mqtt getMqttClient(MqttCallback handler){
            MqttClient client;
        try {
            client = new MqttClient(host, clientId, new MemoryPersistence());
            client.setCallback(handler);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("emqx_test");
            connOpts.setPassword("emqx_test_password".toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);

            // 设置回调
            client.setCallback(handler);

            // 建立连接
            System.out.println("Connecting to broker: " + handler);
            client.connect(connOpts);
            return new Emqx(client);


        }catch (MqttException me){

        }
        return null;
    }


}
