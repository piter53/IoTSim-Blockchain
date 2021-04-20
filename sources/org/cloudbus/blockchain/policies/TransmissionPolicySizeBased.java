package org.cloudbus.blockchain.policies;

import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;

public class TransmissionPolicySizeBased implements TransmissionPolicy{

    private long maxEdgeLetSize;

    public TransmissionPolicySizeBased(long maxEdgeLetSize) {
        this.maxEdgeLetSize = maxEdgeLetSize;
    }

    @Override
    public boolean canTransmitThroughBlockchain(EdgeLet edgeLet) {
        return edgeLet.getCloudletFileSize() <= maxEdgeLetSize;
    }

}
