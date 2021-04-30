package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.network.Network;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
import org.cloudbus.osmosis.core.Flow;

import java.util.List;

public class EdgeBlockchainDataCenter extends EdgeDataCenter {

    public EdgeBlockchainDataCenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    }

    void broadcastTransaction(Flow flow, int tag) {
        for (SimEntity n : Network.getBlockchainNodesList()) {
            sendNow(n.getId(), tag, flow);
        }
    }

}
