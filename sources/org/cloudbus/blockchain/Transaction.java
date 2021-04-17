package org.cloudbus.blockchain;

import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;

import java.util.Random;

public class Transaction {

    // The time when the transaction is created.
    private final long creationTimestamp;
    // The time when the transaction was received.
    private long receptionTimestamp = 0;
    // Sending node
    private final Node senderNode;
    // Reciving node
    private final Node recipentNode;
    // Size of the transaction in MB
    private final int size;
    Transaction(long creationTimestamp, Node senderNode, Node recipentNode, int size){
        this.creationTimestamp = creationTimestamp;
        this.senderNode = senderNode;
        this.recipentNode = recipentNode;
        this.size = size;
    }

    Transaction(Node senderNode, Node recipentNode, int size) {
        this(System.nanoTime(), senderNode, recipentNode, size);
    }

    public void setReceptionTimestamp(long receptionTimestamp) {
        this.receptionTimestamp = receptionTimestamp;
    }
}
