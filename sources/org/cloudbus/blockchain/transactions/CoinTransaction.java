package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.BaseNode;

public class CoinTransaction extends Transaction {

    private final int currencyAmount;

    public CoinTransaction(BaseNode senderNode, BaseNode recipentNode, int currencyAmount){
        super(senderNode, recipentNode);
        this.currencyAmount = currencyAmount;
    }

    public CoinTransaction(double creationTimestamp, BaseNode senderNode, BaseNode recipentNode, int currencyAmount) {
        super(creationTimestamp, senderNode, recipentNode);
        this.currencyAmount = currencyAmount;
    }

    public int getCurrencyAmount() {
        return currencyAmount;
    }
}
