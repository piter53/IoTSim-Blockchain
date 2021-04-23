package org.cloudbus.blockchain.schedule;

import org.cloudbus.blockchain.BlockTest;
import org.cloudbus.blockchain.nodes.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventQueueTest {

    private static EventQueue eventQueue;

    @BeforeAll
    static void setUp() {
         eventQueue = EventQueue.getInstance();
    }

    @BeforeEach
    void setUpEvents() {
        eventQueue.reset();
        int noOfRandEvents = 100;
        for (int i = 0; i < noOfRandEvents; i++) {
            eventQueue.addEvent(EventTest.generateRandomEvent());
        }
    }

    @Test
    void eventsShouldBeInCorrectOrderUponAddition() {
        Event event1 = new Event(Event.EventType.CREATION, new Node(), 100, BlockTest.generateRandomBlock());
        Event event2 = new Event(Event.EventType.CREATION, new Node(), 200, BlockTest.generateRandomBlock());
        Event event3 = new Event(Event.EventType.CREATION, new Node(), 300, BlockTest.generateRandomBlock());
        Event event4 = new Event(Event.EventType.CREATION, new Node(), 400, BlockTest.generateRandomBlock());
        eventQueue.addEvent(event3);
        eventQueue.addEvent(event1);
        eventQueue.addEvent(event2);
        eventQueue.addEvent(event4);
        assertTrue(eventQueue.doesFirstComeBefore(event1, event2));
        assertTrue(eventQueue.doesFirstComeBefore(event2, event3));
        assertTrue(eventQueue.doesFirstComeBefore(event3, event4));

    }

    @Test
    void testReset() {
        assertTrue(eventQueue.getSize()!=0);
        eventQueue.reset();
        assertEquals(eventQueue.getSize(), 0);
    }

    @Test
    void testRemoveEvent() {
        Event event1 = new Event(Event.EventType.CREATION, new Node(), 100, BlockTest.generateRandomBlock());
        eventQueue.addEvent(event1);
        assertTrue(eventQueue.contains(event1));
        eventQueue.removeEvent(event1);
        assertFalse(eventQueue.contains(event1));
    }

    @Test
    void testIsEmpty() {
        assertTrue(!eventQueue.isEmpty());
        eventQueue.reset();
        assertTrue(eventQueue.isEmpty());
    }
}
