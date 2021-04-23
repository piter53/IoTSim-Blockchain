package org.cloudbus.blockchain.schedule;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class EventQueue {

    private static final Comparator<Event> comparator = new EventComparator();
    private static final PriorityQueue<Event> queue = new PriorityQueue<Event>(100, comparator);
    private static final Iterator<Event> iterator = queue.iterator();

    private static EventQueue singleInstance = null;

    private EventQueue() {}

    public static EventQueue getInstance() {
        if (singleInstance == null) {
            singleInstance = new EventQueue();
        }
        return singleInstance;
    }

    public void addEvent(Event event) {
        queue.add(event);
    }

    public Event getNextEvent() {
        return queue.peek();
    }

    public void removeEvent(Event event) {
        queue.remove(event);
    }

    public int getSize() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void reset() {
        queue.clear();
    }

    public boolean contains(Event event) {
        return queue.contains(event);
    }

    public boolean doesFirstComeBefore(Event event1, Event event2) {
        Event e;
        while (!queue.isEmpty()) {
            e = queue.poll();
            if (e == event1) {
                return true;
            }
            if (e == event2) {
                return false;
            }
        }
        throw new IllegalArgumentException("None of the two events were found");
    }

}

class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event event1, Event event2) {
        return Long.compare(event1.getTime(), event2.getTime());
    }
}
