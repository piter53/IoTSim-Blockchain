package org.cloudbus.blockchain.network;

import org.cloudbus.blockchain.devices.IoTBlockchainDevice;

import java.util.HashSet;
import java.util.Set;

public class Network {

    public static Set<IoTBlockchainDevice> getBlockchainNodesList() {
        return blockchainNodesList;
    }

    public static void setBlockchainNodesList(Set<IoTBlockchainDevice> blockchainNodesList) {
        Network.blockchainNodesList = blockchainNodesList;
    }

    private static Set<IoTBlockchainDevice> blockchainNodesList = new HashSet<IoTBlockchainDevice>();
}
