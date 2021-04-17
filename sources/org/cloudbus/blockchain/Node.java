package org.cloudbus.blockchain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Node {
    // Local blockchain
    private LinkedList<Block> blockchain;
    // Local transaction pool
    private ArrayList<Transaction> transactionPool;
    // Total number of mined blocks
    private int noOfMinedBlocks;
    // Currency balance
    private int currencyBalance;

    Node() {
        blockchain = new LinkedList<Block>();
        transactionPool = new ArrayList<Transaction>();
        noOfMinedBlocks = 0;
        currencyBalance = 0;
    }

    public LinkedList<Block> getBlockchain() {
        return blockchain;
    }

    public ArrayList<Transaction> getTransactionPool() {
        return transactionPool;
    }

    public int getNoOfMinedBlocks() {
        return noOfMinedBlocks;
    }

    public int getCurrencyBalance() {
        return currencyBalance;
    }
}
