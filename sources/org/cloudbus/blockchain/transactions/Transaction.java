package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.BlockchainItem;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.consensus.ConsensusAlgorithm;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.Collection;

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
    private final static Network network = Network.getInstance();
    @Getter
    private static final ConsensusAlgorithm consensus = network.getConsensusAlgorithm();

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
            return network.getConsensusAlgorithm().getGlobalTransmissionPolicy().canTransmitThroughBlockchain(o);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasCorrectSenderAndRecipent(Transaction transaction) {
        return network.doesNodeExist(transaction.recipentNode) && network.doesNodeExist(transaction.senderNode);
    }
}
