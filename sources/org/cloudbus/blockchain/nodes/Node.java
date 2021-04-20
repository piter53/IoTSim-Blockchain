package org.cloudbus.blockchain.nodes;

import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Blockchain;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Node {
    // Local blockchain
    private Blockchain blockchain;
    // Local transaction pool
    private Set<Transaction> transactionPool;
    // Currency balance
    private int currencyBalance;

    // Depth of locally maintained blockchain

    private int blockchainDepth;
    public Node() {
        this(0);
    }

    public Node(int blockchainDepth) {
        blockchain = new Blockchain();
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

    /**
     * Update the local blockchain at a given depth, to match miner's version.
     * @param blockchain miner's blockchain version, including newly mined block
     */
    public void updateLocalBlockchain(Blockchain blockchain) {
        Block localBlock = this.blockchain.getLastBlock();
        Block minersBlock = blockchain.getLastBlock();
        outerLoop:
        while (localBlock != null && this.blockchain.getLedger().contains(localBlock)) {
            while (minersBlock != null && blockchain.getLedger().contains(minersBlock)) {
                if (localBlock == minersBlock) {
                    int matchedLocalIndex = this.blockchain.getLedger().indexOf(localBlock);
                    int matchedRemoteIndex = blockchain.getLedger().indexOf(minersBlock);
                    Block blockToBeAdded;
                    for (int i = 0; i < blockchain.getLength() - matchedRemoteIndex; i++) {
                        blockToBeAdded = blockchain.getLedger().get(matchedRemoteIndex + i);
                        this.blockchain.getLedger().add(matchedLocalIndex + i, blockToBeAdded);
                        updateTransactionsPool(blockToBeAdded);
                    }
                    break outerLoop;
                }
                minersBlock = minersBlock.getPreviousBlock();
            }
            localBlock = localBlock.getPreviousBlock();
        }
        trimBlockchain();
    }

    void trimBlockchain() {
        if (blockchainDepth != 0) {
            Iterator<Block> iterator = blockchain.getLedger().iterator();
            int size = blockchain.getLength();
            for (int i = 0; i < (size - blockchainDepth); i++) {
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
        } else {
            // TODO
        }
    }

    /**
     * Update the transactions pool upon receiving new block, i.e. remove
     * transactions that are included in the block.
     * @param block
     */
    public void updateTransactionsPool(Block block) {
        for (Transaction t : block.getTransactionList()) {
            for (Transaction n : transactionPool) {
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
