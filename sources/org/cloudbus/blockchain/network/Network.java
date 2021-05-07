package org.cloudbus.blockchain.network;

import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.devices.EdgeBlockchainDevice;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.Node;
import org.cloudbus.cloudsim.core.SimEntity;

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

    private void addBlockchainNode(BlockchainDevice device) {
        this.blockchainDevicesSet.add(device);
    }

    public void addIoTBlockchainDevices(Set<IoTBlockchainDevice> ioTBlockchainDevices) {
        this.ioTBlockchainDevicesSet.addAll(ioTBlockchainDevices);
        Set<BlockchainDevice> newSet = new HashSet<>(ioTBlockchainDevices);
        addBlockchainNodes(newSet);
    }

    public void addIoTBlockchainDevice(IoTBlockchainDevice device) {
        this.ioTBlockchainDevicesSet.add(device);
        addBlockchainNode(device);
    }

    public void addEdgeBlockchainDevices(Set<EdgeBlockchainDevice> edgeBlockchainDevices) {
        this.edgeBlockchainDataCentersSet.addAll(edgeBlockchainDevices);
        Set<BlockchainDevice> newSet = new HashSet<>(edgeBlockchainDevices);
        addBlockchainNodes(newSet);
    }

    public void addEdgeBlockchainDevice(EdgeBlockchainDevice device) {
        this.edgeBlockchainDataCentersSet.add(device);
        addBlockchainNode(device);
    }

    public Set<BlockchainDevice> getBlockchainDevicesSet() {
        return blockchainDevicesSet;
    }

    public BaseNode getNodeByEntityId(int id) {
        for (BlockchainDevice device : blockchainDevicesSet) {
            if (((SimEntity) device).getId() == id) {
                return device.getNode();
            }
        }
        return null;
    }

    public Set<IoTBlockchainDevice> getIoTBlockchainDevicesSet() {
        return ioTBlockchainDevicesSet;
    }

    public Set<EdgeBlockchainDevice> getEdgeBlockchainDataCentersSet() {
        return edgeBlockchainDataCentersSet;
    }
}
