package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.BlockchainItem;
import org.cloudbus.blockchain.Consensus;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.Collection;

public abstract class Transaction implements BlockchainItem {

    // The time when the transaction is created.
    @Getter
    private final double creationTimestamp;
    // The time when the transaction was received.
    @Getter @Setter
    private long receptionTimestamp = 0;
    // Sending node
    @Getter
    private final BaseNode senderNode;
    // Reciving node
    @Getter
    private final BaseNode recipentNode;
    // Transaction fee
    @Getter
    private final double fee;

    private final static Network network = Network.getInstance();

    Transaction(double creationTimestamp, BaseNode senderNode, BaseNode recipentNode, double fee){
        this.creationTimestamp = creationTimestamp;
        this.senderNode = senderNode;
        this.recipentNode = recipentNode;
        this.fee = fee;
    }

    Transaction(BaseNode senderNode, BaseNode recipentNode, double fee) {
        this(CloudSim.clock(), senderNode, recipentNode, fee);
    }

    public static boolean isTransactionValid(Transaction transaction) {
        return isTransactionFeeCorrect(transaction) &&
            canTransmitThroughBlockchain(transaction);
    }

    private static boolean isTransactionFeeCorrect(Transaction transaction) {
        return transaction.getFee() == calculateTransactionFee(transaction);
    }

    public static boolean isTransactionCollectionValid(Collection<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (!isTransactionValid(transaction)) {
                return false;
            }
        }
        return true;
    }

    public static double calculateTransactionFee(Transaction transaction) {
        if (transaction instanceof CoinTransaction) {
            return Consensus.getTransactionFee();
        } else if (transaction instanceof DataTransaction) {
            return calculateTransactionFee(((DataTransaction) transaction).getSize());
        }
        return 0;
    }

    public static double calculateTransactionFee(long size) {
        return Consensus.getTransactionFee() + size / 500.0;
    }

    public static boolean canTransmitThroughBlockchain(Object o) {
        if (network.getGlobalTransmissionPolicy() != null) {
            return network.getGlobalTransmissionPolicy().canTransmitThroughBlockchain(o);
        } else {
            return true;
        }
    }

}
