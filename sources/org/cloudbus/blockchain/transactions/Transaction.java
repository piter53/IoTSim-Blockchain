package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.BlockchainItem;
import org.cloudbus.blockchain.BlockchainTags;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.consensus.ConsensusProtocol;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Transaction implements BlockchainItem {

    // The time when the transaction is created.
    @Getter
    private final double creationTimestamp;
    // The time when the transaction was received.
    @Getter @Setter
    private double receptionTimestamp = 0;
    // Sending node
    @Getter
    private final BaseNode senderNode;
    // Reciving node
    @Getter
    private final BaseNode recipentNode;
    // Transaction fee
    @Getter @Setter
    private double fee;

    @Getter
    long size = 1;

    @Getter
    final int BROADCAST_TAG = BlockchainTags.BROADCAST_TRANSACTION;

    @Getter
    private final static Network network = Network.getInstance();
    @Getter
    private static final ConsensusProtocol consensus = network.getConsensusProtocol();

    Transaction(double creationTimestamp, BaseNode senderNode, BaseNode recipentNode){
        this.creationTimestamp = creationTimestamp;
        this.senderNode = senderNode;
        this.recipentNode = recipentNode;
    }

    Transaction(BaseNode senderNode, BaseNode recipentNode) {
        this(CloudSim.clock(), senderNode, recipentNode);
    }

    public static boolean isTransactionCollectionValid(Collection<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (!isTransactionValid(transaction)) {
                return false;
            }
        }
        Map<BaseNode, Double> payerNodesToBalanceMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            BaseNode sender = transaction.senderNode;
            BaseNode receiver = transaction.recipentNode;
            if (!payerNodesToBalanceMap.containsKey(sender)) {
                payerNodesToBalanceMap.put(sender, sender.getCurrencyBalance());
            }
            if (!payerNodesToBalanceMap.containsKey(receiver)) {
                payerNodesToBalanceMap.put(receiver, receiver.getCurrencyBalance());
            }
            payerNodesToBalanceMap.put(sender, payerNodesToBalanceMap.get(sender) - transaction.getFee());
            if (transaction instanceof CoinTransaction) {
                payerNodesToBalanceMap.put(sender, payerNodesToBalanceMap.get(sender) - ((CoinTransaction)transaction).getCurrencyAmount());
                payerNodesToBalanceMap.put(receiver, payerNodesToBalanceMap.get(receiver) + ((CoinTransaction)transaction).getCurrencyAmount());
            }
        }
        for (Double balance : payerNodesToBalanceMap.values()) {
            if (balance < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTransactionValid(Transaction transaction) {
        return isTransactionFeeCorrect(transaction) &&
            canBeTransmittedThroughBlockchain(transaction) &&
            hasCorrectSenderAndRecipent(transaction);
    }

    private static boolean isTransactionFeeCorrect(Transaction transaction) {
        return transaction.getFee() == consensus.calculateTransactionFee(transaction);
    }

    public static boolean canBeTransmittedThroughBlockchain(Transaction o) {
        try {
            return network.getConsensusProtocol().getGlobalTransmissionPolicy().canTransmitThroughBlockchain(o);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasCorrectSenderAndRecipent(Transaction transaction) {
        return network.doesNodeExist(transaction.recipentNode) &&
            network.doesNodeExist(transaction.senderNode) &&
            transaction.senderNode != transaction.recipentNode;
    }

    /**
     *
     * @param device Identity of a calling BlockchainDevice
     * @param miner miner of the block in which the transaction got included.
     */
    public void processTransaction(BlockchainDevice device, MinerNode miner){
        if (getRecipentNode() == device.getBlockchainNode()) {
            setReceptionTimestamp(CloudSim.clock());
        } else if (getSenderNode() == device.getBlockchainNode()) {
            device.getBlockchainNode().addBalance(getFee() * -1);
        } else if (miner == device.getBlockchainNode()) {
            device.getBlockchainNode().addBalance(getFee());
        }
    }
}
