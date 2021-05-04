package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.osmosis.core.Flow;

public interface BlockchainDevice {

    void broadcastTransaction(Flow flow, int tag);

    void setBlockchainNode(BaseNode blockchainNode);

    void setTransmissionPolicy(TransmissionPolicy transmissionPolicy);

}
