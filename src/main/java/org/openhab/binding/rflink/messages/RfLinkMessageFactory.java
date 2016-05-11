package org.openhab.binding.rflink.messages;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.openhab.binding.rflink.exceptions.RfLinkException;
import org.openhab.binding.rflink.exceptions.RfLinkNotImpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RfLinkMessageFactory {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(RfLinkMessageFactory.class);

    private static HashMap<String, Class> mapping = new HashMap<>();

    static {

        addMappingOfClass(RfLinkEnergyMessage.class);
        addMappingOfClass(RfLinkWindMessage.class);
        // TODO: add other messages

    }

    private static void addMappingOfClass(Class _class) {

        try {
            RfLinkMessage m = (RfLinkMessage) _class.newInstance();

            for (String key : m.keys()) {
                mapping.put(key, _class);
            }

        } catch (InstantiationException | IllegalAccessException e) {

        }

    }

    public static RfLinkMessage createMessage(String packet) throws RfLinkException, RfLinkNotImpException {

        final RfLinkBaseMessage message = new RfLinkBaseMessage(packet) {
        };

        for (String key : message.values.keySet()) {
            if (mapping.containsKey(key)) {
                try {

                    Class<?> cl = mapping.get(key);
                    Constructor<?> c = cl.getConstructor(String.class);
                    return (RfLinkMessage) c.newInstance(packet);

                } catch (Exception e) {
                    throw new RfLinkException("unable to instanciate message object", e);
                }
            }
        }

        throw new RfLinkNotImpException("No message implementation found for packet " + packet.toString());

    }

}
