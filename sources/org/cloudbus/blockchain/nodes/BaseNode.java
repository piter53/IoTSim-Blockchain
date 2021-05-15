package org.cloudbus.blockchain.nodes;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Blockchain;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.transactions.DataTransaction;
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
        currencyBalance = 0;
        this.blockchainDepth = blockchainDepth;
    }

    public boolean appendLocalBlockchain(Block block) {
        if (blockchain.isBlockValid(block)) {
            if (block.getMiner().getBlockchain().getLastBlock().getPreviousBlock() != getBlockchain().getLastBlock()) {
                updateLocalBlockchain(block.getMiner().getBlockchain());
            }
            else {
                blockchain.addBlockwithoutChecking(block);
            }
            return true;
        }
        return false;
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
        trimBlockchain();
    }

    void trimBlockchain() {
        if (blockchain.getLength() > getBlockchainDepth()) {
            blockchain.setLedger(blockchain.getNMostRecentBlocks(blockchainDepth, true));
        }
    }

    public void appendTransactionsPool(Transaction transaction){}

    public void addBalance(double amount) {
        currencyBalance += amount;
    }

    private static Block getGenesisBlock(){
        return Block.GENESIS_BLOCK;
    }

}
