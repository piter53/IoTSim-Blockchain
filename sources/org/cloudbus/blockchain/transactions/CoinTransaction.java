package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import org.cloudbus.blockchain.devices.BlockchainDevice;
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

    @Override
    public void processTransaction(BlockchainDevice device) {
        if (device.getBlockchainNode() == getRecipentNode()) {
            device.getBlockchainNode().addBalance(getCurrencyAmount());
        } else if (device.getBlockchainNode() == getSenderNode()) {
            device.getBlockchainNode().addBalance(getCurrencyAmount() * -1);
        }
    }
}
