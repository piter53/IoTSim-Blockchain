package org.cloudbus.blockchain.nodes;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.*;

public class MinerNode extends BaseNode {

    // Total number of mined blocks
    @Getter
    private int noOfMinedBlocks;

    @Getter @Setter
    private long hashpower;

    // Local transaction pool
    @Getter
    private Queue<Transaction> transactionPool;

    public MinerNode() {
        this(0, (long)0);
    }

    public MinerNode(Integer blockchainDepth, Long hashpower) {
        super(blockchainDepth);
        noOfMinedBlocks = 0;
        this.hashpower = hashpower;
        // Transactions are inserted into a queue sorted by creation time.
        transactionPool = new PriorityQueue<Transaction>(1, Network.getInstance().getConsensusProtocol().getTransactionComparator());
    }

    private Collection<Transaction> getTransactionsForNewBlock() {
        long maxSize = Network.getInstance().getConsensusProtocol().getMaxBlockSize();
        Set<Transaction> appendableTransactions = new HashSet<>();
        long currentSize=0;
        while (true) {
            if (!getTransactionPool().isEmpty()) {
                if (!((currentSize + getTransactionPool().peek().getSizeMB()) <= maxSize))
                    break;
                Transaction transaction = getTransactionPool().poll();
                appendableTransactions.add(transaction);
                currentSize += transaction.getSizeMB();
            } else {
                break;
            }
        }
        return appendableTransactions;
    }

    public Block mineBlock(BlockchainDevice device) {
        Collection<Transaction> transactions = getTransactionsForNewBlock();
        if (!transactions.isEmpty()) {
            Block newBlock = new Block(getBlockchain().getLastBlock(), this, transactions);
            getBlockchain().addBlockwithoutChecking(newBlock);
            device.processAcceptedTransactions(newBlock);
            trimBlockchain();
            device.broadcastBlockchainItem(newBlock);
            noOfMinedBlocks++;
            return newBlock;
        }
        return null;
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
        }
    }

    @Override
    public void appendTransactionsPool(Transaction transaction) {
        transactionPool.add(transaction);
    }

    public void appendTransactionsPool(Collection<Transaction> transactions) {
        transactionPool.addAll(transactions);
    }

    public void updateTransactionsPool() {
        for (Block b : getBlockchain().getLedger()) {
            updateTransactionsPool(b);
        }
    }

    @Override
    public boolean appendLocalBlockchain(Block block) {
        if (super.appendLocalBlockchain(block)) {
            updateTransactionsPool();
            return true;
        }
        return false;
    }
}
