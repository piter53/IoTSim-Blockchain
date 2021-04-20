package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;

public class CoinTransaction extends Transaction {

    private final int currencyAmount;

    public CoinTransaction(Node senderNode, Node recipentNode, int currencyAmount){
        super(senderNode, recipentNode);
        this.currencyAmount = currencyAmount;
    }

    public CoinTransaction(long creationTimestamp, Node senderNode, Node recipentNode, int currencyAmount) {
        super(creationTimestamp, senderNode, recipentNode);
        this.currencyAmount = currencyAmount;
    }

    public int getCurrencyAmount() {
        return currencyAmount;
    }
}
