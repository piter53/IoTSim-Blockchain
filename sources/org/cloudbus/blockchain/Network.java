package org.cloudbus.blockchain;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.consensus.ConsensusProtocol;
import org.cloudbus.blockchain.consensus.ProofOfWork;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicySizeBased;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.devices.EdgeBlockchainDevice;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.cloudsim.core.SimEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public class Network {

    @Getter
    private ConsensusProtocol consensusProtocol;
    @Getter @Setter
    private Collection<BlockchainDevice> blockchainDevicesSet;
    @Getter
    private Collection<IoTBlockchainDevice> ioTBlockchainDevicesSet;
    @Setter
    private Collection<EdgeBlockchainDevice> edgeBlockchainDataCentersSet;
    @Getter
    private Collection<BlockchainDevice> minerDevices;
    private static Network singleInstance = null;

    private Network(ConsensusProtocol consensusProtocol) {
        blockchainDevicesSet = new HashSet<>();
        ioTBlockchainDevicesSet = new HashSet<>();
        edgeBlockchainDataCentersSet = new HashSet<>();
        minerDevices = new HashSet<>();
        this.consensusProtocol = consensusProtocol;
    }

    public static Network getInstance() {
        if (singleInstance == null) {
            singleInstance = new Network(new ProofOfWork(new TransmissionPolicySizeBased((long)100), 10.0, 1, 1, 0.001, 0.01, 100));
        }
        return singleInstance;
    }

    public static void setConsensus(ConsensusProtocol consensusProtocol) {
        getInstance().consensusProtocol = consensusProtocol;
    }

    public BlockchainDevice pickNewMiningDevice() {
        Set<MinerNode> minerNodes = new HashSet<>();
        for (BlockchainDevice device : minerDevices) {
            minerNodes.add((MinerNode) device.getBlockchainNode());
        }
        MinerNode miner = consensusProtocol.pickMiner(minerNodes);
        for (BlockchainDevice device : minerDevices) {
            if (miner == device.getBlockchainNode()) {
                return device;
            }
        }
        return null;
    }

    private void addBlockchainNodes(Set<BlockchainDevice> blockchainDevices) {
        for (BlockchainDevice device: blockchainDevices){
            addBlockchainNode(device);
        }
    }

    private void addBlockchainNode(BlockchainDevice device) {
        if (device.getBlockchainNode() instanceof MinerNode) {
            minerDevices.add(device);
        }
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

    public boolean existPendingTransactions() {
        for (BlockchainDevice miner : minerDevices) {
            if (!((MinerNode) miner.getBlockchainNode()).getTransactionPool().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Blockchain getLongestBlockchain(){
        Blockchain blockchain = new Blockchain();
        for (BlockchainDevice device : getBlockchainDevicesSet()){
            if (blockchain.getLedger().isEmpty() || blockchain.getLastBlock().getGenerationTimestamp() < device.getBlockchainNode().getBlockchain().getLastBlock().getGenerationTimestamp()) {
                blockchain = device.getBlockchainNode().getBlockchain();
            }
        }
        return blockchain;
    }
}
