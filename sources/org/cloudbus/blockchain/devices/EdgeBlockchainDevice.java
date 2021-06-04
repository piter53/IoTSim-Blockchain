package org.cloudbus.blockchain.devices;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicy;
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

    @Getter @Setter
    private BaseNode blockchainNode;
    @Getter @Setter
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
        if (!processBlockchainEvent(event)){
            super.processEvent(event);
        }
    }

    @Override
    public void sendNow(int id, int tag, Object o) {
        super.sendNow(id, tag, o);
    }

}
