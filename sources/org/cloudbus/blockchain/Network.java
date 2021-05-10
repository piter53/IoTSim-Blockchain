package org.cloudbus.blockchain;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.devices.EdgeBlockchainDevice;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.blockchain.policies.TransmissionPolicySizeBased;
import org.cloudbus.blockchain.transactions.Transaction;
import org.cloudbus.cloudsim.core.SimEntity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public class Network {

    @Getter @Setter
    private Set<BlockchainDevice> blockchainDevicesSet;
    @Getter
    private Double blockInterval = 20.0;
    @Getter
    private Comparator<Transaction> transactionComparator = Comparator.comparingDouble(Transaction::getCreationTimestamp);
    @Getter
    private long maxBlockSize = 1000;
    @Getter
    private TransmissionPolicy globalTransmissionPolicy = new TransmissionPolicySizeBased((long)100);
    @Getter
    private Set<IoTBlockchainDevice> ioTBlockchainDevicesSet;
    @Setter
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

    public MinerNode pickNewMiner() {

    }

    private void addBlockchainNodes(Set<BlockchainDevice> blockchainDevices) {
        this.blockchainDevicesSet.addAll(blockchainDevices);
    }

    private void addBlockchainNode(BlockchainDevice device) {
        this.blockchainDevicesSet.add(device);
    }

    public boolean doesNodeExist(BaseNode node) {
        for (BlockchainDevice device : blockchainDevicesSet) {
            if (device.getBlockchainNode() == node) {
                return true;
            }
        }
        return false;
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

    public BaseNode getNodeByEntityId(int id) {
        for (BlockchainDevice device : blockchainDevicesSet) {
            if (((SimEntity) device).getId() == id) {
                return device.getBlockchainNode();
            }
        }
        return null;
    }

}
