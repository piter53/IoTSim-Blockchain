package org.cloudbus.blockchain.schedule;

import org.cloudbus.blockchain.BlockTest;
import org.cloudbus.blockchain.nodes.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventComparatorTest {

    @Test
    void testCompareTo () {
        EventComparator comparator = new EventComparator();
        Event event1 = new Event(Event.EventType.CREATION, new Node(), 100, BlockTest.generateRandomBlock());
        Event event2 = new Event(Event.EventType.CREATION, new Node(), 200, BlockTest.generateRandomBlock());
        assertEquals(comparator.compare(event1,event2), -1);
        assertEquals(comparator.compare(event2,event1), 1);
    }

}
