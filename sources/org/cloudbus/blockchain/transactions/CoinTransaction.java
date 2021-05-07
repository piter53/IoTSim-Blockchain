package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import org.cloudbus.blockchain.nodes.BaseNode;

public class CoinTransaction extends Transaction {

    @Getter
    private final int currencyAmount;

    public CoinTransaction(BaseNode senderNode, BaseNode recipentNode, int currencyAmount, double fee){
        super(senderNode, recipentNode, fee);
        this.currencyAmount = currencyAmount;
    }

    public CoinTransaction(double creationTimestamp, BaseNode senderNode, BaseNode recipentNode, int currencyAmount, double fee) {
        super(creationTimestamp, senderNode, recipentNode, fee);
        this.currencyAmount = currencyAmount;
    }

}
