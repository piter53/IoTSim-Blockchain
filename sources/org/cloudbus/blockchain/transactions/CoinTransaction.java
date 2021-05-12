package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.cloudsim.core.CloudSim;

public class CoinTransaction extends Transaction {

    @Getter
    private final int currencyAmount;

    public CoinTransaction(BaseNode senderNode, BaseNode recipentNode, int currencyAmount){
        this(CloudSim.clock(), senderNode, recipentNode, currencyAmount);
    }

    public CoinTransaction(double creationTimestamp, BaseNode senderNode, BaseNode recipentNode, int currencyAmount) {
        super(creationTimestamp, senderNode, recipentNode);
        this.currencyAmount = currencyAmount;
        this.setFee(getConsensus().calculateTransactionFee(this));
    }

}
