package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;
import org.cloudbus.cloudsim.core.CloudSim;

public abstract class Transaction {

    // The time when the transaction is created.
    private final double creationTimestamp;
    // The time when the transaction was received.
    private long receptionTimestamp = 0;
    // Sending node
    private final Node senderNode;
    // Reciving node
    private final Node recipentNode;

    Transaction(double creationTimestamp, Node senderNode, Node recipentNode){
        this.creationTimestamp = creationTimestamp;
        this.senderNode = senderNode;
        this.recipentNode = recipentNode;
    }

    Transaction(Node senderNode, Node recipentNode) {
        this(CloudSim.clock(), senderNode, recipentNode);
    }

    public void setReceptionTimestamp(long receptionTimestamp) {
        this.receptionTimestamp = receptionTimestamp;
    }
}
