package org.cloudbus.blockchain.nodes;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.transactions.Transaction;

import javax.print.DocFlavor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MinerNode extends BaseNode {

    // Total number of mined blocks
    @Getter
    private int noOfMinedBlocks;

    @Getter @Setter
    private long hashpower;

    public MinerNode() {
        this(0, (long)0);
    }

    public MinerNode(Integer blockchainDepth, Long hashpower) {
        super(blockchainDepth);
        noOfMinedBlocks = 0;
    }

    private Collection<Transaction> getTransactionsForNewBlock() {
        long maxSize = Network.getInstance().getMaxBlockSize();
        Set<Transaction> appendableTransactions = new HashSet<>();
        long currentSize=0;
        while (true) {
            assert getTransactionPool().peek() != null;
            if (!((currentSize + getTransactionPool().peek().getSize()) <= maxSize))
                break;
            Transaction transaction = getTransactionPool().poll();
            appendableTransactions.add(transaction);
            currentSize += transaction.getSize();
        }
        return appendableTransactions;
    }

    public Block mineBlock(BlockchainDevice device) {
        Collection<Transaction> transactions = getTransactionsForNewBlock();
        Block newBlock = new Block(getBlockchain().getLastBlock(),this, transactions);
        getBlockchain().getLedger().add(newBlock);
        device.broadcastBlockchainItem(newBlock);
        noOfMinedBlocks++;
        return newBlock;
    }

}
