package cc.iotkit.testemqx;

public interface Mqtt {
    void subscribe(String topic);
    void publish(String msg);
}
