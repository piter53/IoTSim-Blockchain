package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.BlockchainTags;
import org.cloudbus.blockchain.network.Network;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
import org.cloudbus.osmosis.core.Flow;

import java.util.List;

/**
 * This class represents EdgeDataCenter deployed as a blockchain node.
 * Type of node is defined by BaseNode field, and deployed TransmissionPolicy
 * by transmissionPolicy field.
 */
public class EdgeBlockchainDevice extends EdgeDataCenter implements BlockchainDevice {

    private static Network blockchainNetwork = Network.getInstance();
    private BaseNode blockchainNode;
    private TransmissionPolicy transmissionPolicy;

    public EdgeBlockchainDevice(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval, BaseNode node, TransmissionPolicy transmissionPolicy) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
        this.blockchainNode = node;
        this.transmissionPolicy = transmissionPolicy;
    }

    @Override
    public void broadcastTransaction(Flow flow) {
        for (BlockchainDevice n : blockchainNetwork.getBlockchainDevicesSet()) {
            sendNow(((SimEntity)n).getId(), BlockchainTags.BROADCAST_TRANSACTION, flow);
        }
    }

    @Override
    public void setBlockchainNode(BaseNode blockchainNode) {
        this.blockchainNode = blockchainNode;
    }

    @Override
    public void setTransmissionPolicy(TransmissionPolicy transmissionPolicy) {
        this.transmissionPolicy = transmissionPolicy;
    }
}
