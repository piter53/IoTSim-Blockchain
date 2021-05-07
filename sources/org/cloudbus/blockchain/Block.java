package org.cloudbus.blockchain;

import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.transactions.Transaction;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.Set;

public class Block implements BlockchainItem {
    private final Block previousBlock;
    private final double timestamp;
    private final MinerNode miner;

    private final Set<Transaction> transactionList;

    private final boolean isGenesis;
    public Block(Block previousBlock, MinerNode miner, Set<Transaction> transactionList){
        if (!(previousBlock == null)) {
            this.previousBlock = previousBlock;
            isGenesis = false;
        } else {
            this.previousBlock = null;
            isGenesis = true;
        }
        timestamp = CloudSim.clock();
        this.miner = miner;
        this.transactionList = transactionList;
    }

    public Block getPreviousBlock() {
        return previousBlock;
    }

    public Set<Transaction> getTransactionList() {
        return transactionList;
    }

    public int getSize(){
        return transactionList.size();
    }
}
