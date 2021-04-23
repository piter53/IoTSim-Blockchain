package org.cloudbus.blockchain.schedule;

import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.BlockTest;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.nodes.Node;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private static final Random random = new Random();
    public static Event generateRandomEvent() {
        Event.EventType eventType;
        if (random.nextBoolean()) {
            eventType = Event.EventType.CREATION;
        } else {
            eventType = Event.EventType.RECEPTION;
        }
        return new Event(eventType, new Node(), random.nextLong(), BlockTest.generateRandomBlock());
    }

}
