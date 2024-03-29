package cc.iotkit.plugins.mqtt.service;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * mqtt设备下行接口
 *
 * @author sjg
 */
@Slf4j
@Service
public class MqttDevice implements IDevice {

    @Autowired
    private MqttVerticle mqttVerticle;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        String topic = String.format("/sys/%s/%s/c/service/property/get", action.getProductKey(), action.getDeviceName());
        return send(
                topic,
                action.getDeviceName(),
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service.property.get")
                        .put("params", action.getKeys())
                        .toString()
        );
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        String topic;
        String endpointMapKey;
//        if (NodeType.GATEWAY_DEVICE.equals(action.getNodeType())){
            log.info("=================================GATEWAY_DEVICE");
            if(mqttVerticle.haveEndpoint(action.getDeviceName())){
                endpointMapKey=action.getDeviceName();
                log.info("=================================DeviceName:"+endpointMapKey);
            }else {
                endpointMapKey=action.getProductKey();
                log.info("=================================ProductKey:"+endpointMapKey);
            }
            topic = String.format("/sys/%s/%s/c/service/property/set", action.getProductKey(), action.getDeviceName());
//        }else{
//            log.info("=================================NOT_GATEWAY_DEVICE");
//            topic = String.format("/sys/%s/%s/c/service/property/set", action.getProductKey(), action.getDeviceName());
//            endpointMapKey=action.getDeviceName();
//        }

        return send(
                topic,
                endpointMapKey,
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service.property.set")
                        .put("deviceName",action.getDeviceName())
                        .put("params", action.getParams())
                        .toString()
        );
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        String topic = String.format("/sys/%s/%s/c/service/%s", action.getProductKey(), action.getDeviceName(), action.getName());
        return send(
                topic,
                action.getDeviceName(),
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service." + action.getName())
                        .put("params", action.getParams())
                        .toString()
        );
    }

    private ActionResult send(String topic, String endpointMapKey, String payload) {
        try {
            mqttVerticle.publish(
                    endpointMapKey,
                    topic,
                    payload
            );
            return ActionResult.builder().code(0).reason("").build();
        } catch (BizException e) {
            return ActionResult.builder().code(e.getCode()).reason(e.getMessage()).build();
        } catch (Exception e) {
            return ActionResult.builder().code(ErrCode.UNKNOWN_EXCEPTION.getKey()).reason(e.getMessage()).build();
        }
    }

}
