package org.cloudbus.blockchain.policies;

import org.cloudbus.blockchain.schedule.Event;
import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;
import org.cloudbus.osmosis.core.Flow;

public class TransmissionPolicySizeBased implements TransmissionPolicy{

    private long maxEdgeLetSize;

    public TransmissionPolicySizeBased(Long maxEdgeLetSize) {
        this.maxEdgeLetSize = maxEdgeLetSize;
    }

    @Override
    public boolean canTransmitThroughBlockchain(Object o) {
        Flow event;
        try {
            event = (Flow) o;
            return event.getAmountToBeProcessed() <= maxEdgeLetSize;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        // TODO different object types
        return false;
    }

}
