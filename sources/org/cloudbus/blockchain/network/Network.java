package org.cloudbus.blockchain.network;

import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.devices.EdgeBlockchainDevice;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;

import java.util.HashSet;
import java.util.Set;

public class Network {

    private Set<BlockchainDevice> blockchainDevicesSet;

    private Set<IoTBlockchainDevice> ioTBlockchainDevicesSet;

    private Set<EdgeBlockchainDevice> edgeBlockchainDataCentersSet;
    private static Network singleInstance = null;

    private Network() {
        blockchainDevicesSet = new HashSet<>();
        ioTBlockchainDevicesSet = new HashSet<>();
        edgeBlockchainDataCentersSet = new HashSet<>();
    }

    public static Network getInstance() {
        if (singleInstance == null) {
            singleInstance = new Network();
        }
        return singleInstance;
    }

    private void addBlockchainNodes(Set<BlockchainDevice> blockchainDevices) {
        this.blockchainDevicesSet.addAll(blockchainDevices);
    }

    public void addIoTBlockchainDevices(Set<IoTBlockchainDevice> ioTBlockchainDevices) {
        this.ioTBlockchainDevicesSet.addAll(ioTBlockchainDevices);
        Set<BlockchainDevice> newSet = new HashSet<>(ioTBlockchainDevices);
        addBlockchainNodes(newSet);
    }

    public void addEdgeBlockchainDevices(Set<EdgeBlockchainDevice> edgeBlockchainDevices) {
        this.edgeBlockchainDataCentersSet.addAll(edgeBlockchainDevices);
        Set<BlockchainDevice> newSet = new HashSet<>(edgeBlockchainDevices);
        addBlockchainNodes(newSet);
    }

    public Set<BlockchainDevice> getBlockchainDevicesSet() {
        return blockchainDevicesSet;
    }

    public Set<IoTBlockchainDevice> getIoTBlockchainDevicesSet() {
        return ioTBlockchainDevicesSet;
    }

    public Set<EdgeBlockchainDevice> getEdgeBlockchainDataCentersSet() {
        return edgeBlockchainDataCentersSet;
    }
}
