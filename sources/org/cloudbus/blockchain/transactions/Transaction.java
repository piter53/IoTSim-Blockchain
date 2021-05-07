package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.BlockchainItem;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.cloudsim.core.CloudSim;

public abstract class Transaction implements BlockchainItem {

    // The time when the transaction is created.
    private final double creationTimestamp;
    // The time when the transaction was received.
    private long receptionTimestamp = 0;
    // Sending node
    private final BaseNode senderNode;
    // Reciving node
    private final BaseNode recipentNode;

    Transaction(double creationTimestamp, BaseNode senderNode, BaseNode recipentNode){
        this.creationTimestamp = creationTimestamp;
        this.senderNode = senderNode;
        this.recipentNode = recipentNode;
    }

    Transaction(BaseNode senderNode, BaseNode recipentNode) {
        this(CloudSim.clock(), senderNode, recipentNode);
    }

    public void setReceptionTimestamp(long receptionTimestamp) {
        this.receptionTimestamp = receptionTimestamp;
    }
}
