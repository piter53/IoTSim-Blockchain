package org.cloudbus.blockchain;

import lombok.Getter;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.transactions.Transaction;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.Collection;

public class Block implements BlockchainItem {
    @Getter
    private final Block previousBlock;
    @Getter
    private final double generationTimestamp;
    @Getter
    private final MinerNode miner;
    private final boolean isGenesis;
    @Getter
    private final Collection<Transaction> transactionList;
    @Getter
    private long sizeMB = 0;
    @Getter
    final int BROADCAST_TAG = BlockchainTags.BROADCAST_BLOCK;

    public static final Block GENESIS_BLOCK = new Block(null, null, null);
    private final static Network network = Network.getInstance();


    public Block(Block previousBlock, MinerNode miner, Collection<Transaction> transactionList){
        if (!(previousBlock == null)) {
            this.previousBlock = previousBlock;
            isGenesis = false;
        } else {
            this.previousBlock = null;
            isGenesis = true;
        }
        generationTimestamp = CloudSim.clock();
        this.miner = miner;
        this.transactionList = transactionList;
        if (transactionList != null){
            for (Transaction transaction : transactionList) {
                sizeMB += transaction.getSizeMB();
            }
        }
    }

    public static boolean isBlockValid(Block block) {
        if (!block.isGenesis()) {
            return Transaction.isTransactionCollectionValid(block.getTransactionList()) &&
                hasValidMiner(block);
        } else {
            return true;
        }
    }

    public static boolean hasValidMiner(Block block) {
        return network.doesNodeExist(block.miner);
    }

    public boolean isGenesis() {
        return isGenesis;
    }
}
