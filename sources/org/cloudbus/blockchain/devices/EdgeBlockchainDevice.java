package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.BlockchainTags;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.blockchain.transactions.Transaction;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;

import java.util.List;

/**
 * This class represents EdgeDataCenter deployed as a blockchain node.
 * Type of node is defined by BaseNode field, and deployed TransmissionPolicy
 * by transmissionPolicy field.
 */
public class EdgeBlockchainDevice extends EdgeDataCenter implements BlockchainDevice {

    private BaseNode blockchainNode;
    private TransmissionPolicy transmissionPolicy;

    public EdgeBlockchainDevice(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval, BaseNode node, TransmissionPolicy transmissionPolicy) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
        this.blockchainNode = node;
        this.transmissionPolicy = transmissionPolicy;
    }

    public EdgeBlockchainDevice(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, Double schedulingInterval) throws Exception{
        this(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, null, null);
    }

    @Override
    public void processEvent(SimEvent event) {
        int tag = event.getTag();
        switch (tag) {
            case BlockchainTags.BROADCAST_TRANSACTION: {
                appendTransactionPool((Transaction) event.getData());
                break;
            }
            default: {
                super.processEvent(event);
            }
        }

    }

    @Override
    public BaseNode getBlockchainNode() {
        return blockchainNode;
    }

    @Override
    public void sendNow(int id, int tag, Object o) {
        super.sendNow(id, tag, o);
    }

    @Override
    public TransmissionPolicy getTransmissionPolicy() {
        return transmissionPolicy;
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
