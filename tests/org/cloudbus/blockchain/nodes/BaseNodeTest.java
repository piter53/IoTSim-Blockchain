package org.cloudbus.blockchain.nodes;

import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Blockchain;
import org.cloudbus.blockchain.BlockchainTest;
import org.cloudbus.blockchain.transactions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    private Node node;

    @BeforeEach
    public void setUp() {
        node = new Node();
    }

    @Test
    void getBlockchain() {
    }

    @Test
    void getTransactionPool() {
    }

    @Test
    void getCurrencyBalance() {
    }

    @Test
    void testTrimBlockchain(){
        Random random = new Random();
        for (int i = 5; i < 100; i+=5) {
            node = new Node(i);
            Blockchain blockchain = BlockchainTest.generateBlockchainWithNLinkedBlocks(i + random.nextInt(40));
            Blockchain blockchain1 = new Blockchain(blockchain.getNMostRecentBlocks(node.getBlockchainDepth(), true));
            node.setBlockchain(blockchain);
            node.trimBlockchain();
            assertEquals(i, node.getBlockchain().getLength());
            for (int j = 0; j < node.getBlockchain().getLength(); j++) {
                assertEquals(node.getBlockchain().getLedger().get(j), blockchain1.getLedger().get(j));
            }
        }

    }

    @Test
    void testUpdateLocalBlockchain() {
        int noOfRemoteBlocks = 30;
        int depth = 10;
        Blockchain remoteBlockchain = BlockchainTest.generateBlockchainWithNLinkedBlocks(noOfRemoteBlocks);
        ArrayList<Block> expectedLocalList = remoteBlockchain.getNMostRecentBlocks(depth, true);
        // check if first (most recent) element of extracted list is the one it should
        assertEquals(expectedLocalList.get(expectedLocalList.size() - 1), remoteBlockchain.getLastBlock());
        // same, but for the oldest element
        assertEquals(expectedLocalList.get(0), remoteBlockchain.getLedger().get(remoteBlockchain.getLength() - depth));
        assertEquals(depth, expectedLocalList.size());
        node = new Node(depth);
        int noOfLackingBlocks = 3;
        // Get 7 out of 10 most recent blocks, by getting 10 first, and removing last 3
        ArrayList<Block> localOutdatedList = remoteBlockchain.getNMostRecentBlocks(depth + noOfLackingBlocks, true);
        int listSize = localOutdatedList.size();
        for (int i = 0; i < noOfLackingBlocks; i++) {
            localOutdatedList.remove(listSize - 1 - i);
        }
        assertEquals(depth, localOutdatedList.size());

        Blockchain localBlockchain = new Blockchain(localOutdatedList);
        node.setBlockchain(localBlockchain);

        node.updateLocalBlockchain(remoteBlockchain);

        assertEquals(expectedLocalList, node.getBlockchain().getLedger());
    }

//    @Test
//    void testUpdateTransactionsPool() {
//        Collection<Transaction> transactionPool = TransactionTest.generateRandomTransactionSet(50);
//        node.appendTransactionsPool(transactionPool);
//        Set<Transaction> dataTransactions = new HashSet<>();
//        Set<Transaction> coinTransactions = new HashSet<>();
//        for (Transaction t : transactionPool) {
//            if (t instanceof DataTransaction){
//                dataTransactions.add(t);
//            } else if (t instanceof CoinTransaction) {
//                coinTransactions.add(t);
//            }
//        }
//        Block block = new Block(null, new MinerNode(), dataTransactions);
//        node.updateTransactionsPool(block);
//        assertNotSame(coinTransactions, node.getTransactionPool());
//        assertEquals(coinTransactions, node.getTransactionPool());
//    }
}
