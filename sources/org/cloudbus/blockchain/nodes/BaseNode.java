package org.cloudbus.blockchain.nodes;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Blockchain;
import org.cloudbus.blockchain.Consensus;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.*;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public abstract class BaseNode {
    // Local blockchain
    @Setter @Getter
    private Blockchain blockchain;
    // Local transaction pool
    @Getter
    private Queue<Transaction> transactionPool;
    // Currency balance
    @Getter
    private double currencyBalance;
    // Depth of locally maintained blockchain
    @Getter
    private int blockchainDepth;

    public BaseNode() {
        this(0);
    }

    public BaseNode(Integer blockchainDepth) {
        blockchain = new Blockchain();
        blockchain.addBlock(getGenesisBlock());
        // Transactions are inserted into a queue sorted by creation time.
        transactionPool = new PriorityQueue<Transaction>(1, Network.getInstance().getTransactionComparator());
        currencyBalance = 0;
        this.blockchainDepth = blockchainDepth;
    }

    public void appendLocalBlockchain(Block block) {
        if (blockchain.addBlock(block)) {

            updateTransactionsPool(block);
        }
    }

    /**
     * Update the local blockchain at a given depth, to match miner's version.
     * @author Piotr Grela, based on original python implementation in BlockSim
     * @param blockchain miner's blockchain version, including newly mined block
     */
    public void updateLocalBlockchain(Blockchain blockchain) {
        int depth = getBlockchainDepth();
        ArrayList<Block> trimmed = blockchain.getNMostRecentBlocks(depth, true);
        this.blockchain.setLedger(trimmed);
        for (Block b : blockchain.getLedger()) {
            updateTransactionsPool(b);
        }
        trimBlockchain();
    }

    void trimBlockchain() {
        if (blockchain.getLength() > getBlockchainDepth()) {
            blockchain.setLedger(blockchain.getNMostRecentBlocks(blockchainDepth, true));
        }
    }

    /**
     * Update the transactions pool upon receiving new block, i.e. remove
     * transactions that are included in the block.
     * @author Piotr Grela, based on original python implementation in BlockSim
     * @param block
     */
    public void updateTransactionsPool(Block block) {
        if (!(block.getTransactionList() == null || block.getTransactionList().isEmpty())) {
            for (Object t : block.getTransactionList()) {
                for (Object n : transactionPool) {
                    if (t == n) {
                        transactionPool.remove(n);
                        break;
                    }
                }
            }
        }    }

    public void appendTransactionsPool(Transaction transaction) {
        transactionPool.add(transaction);
    }

    public void appendTransactionsPool(Collection<Transaction> transactions) {
        transactionPool.addAll(transactions);
    }

    private static Block getGenesisBlock(){
        return Block.GENESIS_BLOCK;
    }

}
