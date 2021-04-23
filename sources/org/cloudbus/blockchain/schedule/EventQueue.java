package org.cloudbus.blockchain.schedule;

import java.util.Comparator;
import java.util.PriorityQueue;

public class EventQueue {

    private static final Comparator<Event> comparator = new EventComparator();
    private static final PriorityQueue<Event> queue = new PriorityQueue<Event>(100, comparator);

    private EventQueue() {}

    public void addEvent(Event event) {
        queue.add(event);
    }

    public Event getNextEvent() {
        return queue.peek();
    }

    public void removeEvent(Event event){
        queue.remove(event);
    }

    public int getSize() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}

class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event event1, Event event2) {
        if (event1.getTime() < event2.getTime()) {
            return -1;
        }
        if (event1.getTime() > event2.getTime()) {
            return 1;
        }
        return 0;
    }
}
