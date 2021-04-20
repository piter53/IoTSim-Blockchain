package org.cloudbus.blockchain.policies;

import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;

public interface TransmissionPolicy {

    public boolean canTransmitThroughBlockchain(EdgeLet edgeLet);
}
