package org.cloudbus.blockchain;

import java.util.ArrayList;

public class Block {
    private final Block previousBlock;
    private final long timestamp;
    private final Node miner;
    private final ArrayList<Transaction> transactionList;
    private final int size;

    Block(Block previousBlock, Node miner, ArrayList<Transaction> transactionList, int size){
        this.previousBlock = previousBlock;
        timestamp = System.nanoTime();
        this.miner = miner;
        this.transactionList = transactionList;
        this.size = size;
    }
}
