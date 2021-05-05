package org.cloudbus.blockchain.nodes;

import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Blockchain;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseNode {
    // Genesis block
    private static final Block genesisBlock = new Block(null, null, null);
    // Local blockchain
    private Blockchain blockchain;
    // Local transaction pool
    private Set<Transaction> transactionPool;
    // Currency balance
    private int currencyBalance;

    // Depth of locally maintained blockchain

    private int blockchainDepth;
    public BaseNode() {
        this(0);
    }

    public BaseNode(int blockchainDepth) {
        blockchain = new Blockchain();
        blockchain.addBlock(getGenesisBlock());
        transactionPool = new HashSet<>();
        currencyBalance = 0;
        this.blockchainDepth = blockchainDepth;
    }

    public void addTransaction(Transaction transaction){
        transactionPool.add(transaction);
    }

    void setBlockchain(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public Set<Transaction> getTransactionPool() {
        return transactionPool;
    }

    public void setTransactionPool(Set<Transaction> transactionPool) {
        this.transactionPool = transactionPool;
    }

    public int getCurrencyBalance() {
        return currencyBalance;
    }

    private static Block getGenesisBlock(){
        return genesisBlock;
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
        for (Object t : block.getTransactionList()) {
            for (Object n : transactionPool) {
                if (t == n) {
                    transactionPool.remove(n);
                    break;
                }
            }
        }
    }

    public int getBlockchainDepth() {
        return blockchainDepth;
    }

}
