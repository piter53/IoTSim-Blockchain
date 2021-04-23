package org.cloudbus.blockchain.schedule;

import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.Node;

public class Event {

    private final EventType type;
    private final BaseNode node;

    private final long time;

    private final Block block;


    enum EventType{
        CREATION,
        RECEPTION;
    }
    public Event(EventType type, BaseNode node, long time, Block block) {
        this.type = type;
        this.node = node;
        this.time = time;
        this.block = block;
    }

    public long getTime() {
        return time;
    }
}
