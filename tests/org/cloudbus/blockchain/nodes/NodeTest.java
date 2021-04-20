package org.cloudbus.blockchain.nodes;

import org.cloudbus.blockchain.Block;
import org.cloudbus.blockchain.Blockchain;
import org.cloudbus.blockchain.BlockchainTest;
import org.cloudbus.blockchain.transactions.*;
import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;
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
            Blockchain blockchain1 = new Blockchain();
            for (int j = 0; j < node.getBlockchainDepth(); j++) {
                blockchain1.addBlock(blockchain.getLedger().get(node.getBlockchainDepth() - 1 - j));
            }
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
        ArrayList<Block> remoteList = new ArrayList<Block>();
        Block previousBlock=null, block;
        for (int i = 0; i < 30; i++) {
            block = new Block(previousBlock, new MinerNode(), new HashSet<>());
            previousBlock = block;
            remoteList.add(block);
        }
        int depth = 10;
        ArrayList<Block> expectedLocalList = new ArrayList<Block>();
        for (int i = 20; i < 30; i++) {
            expectedLocalList.add(remoteList.get(i));
        }
        assertEquals(depth, expectedLocalList.size());

        Blockchain remoteBlockchain = new Blockchain(remoteList);

        node = new Node(depth);
        ArrayList<Block> localList = new ArrayList<Block>();
        int noOfLackingBlocks = 3;
        for (int i = 0; i < depth; i++) {
            localList.add(i, remoteList.get(remoteList.size() - (1 + depth + noOfLackingBlocks) + i));
        }
        assertEquals(depth, localList.size());

        Blockchain localBlockchain = new Blockchain(localList);
        node.setBlockchain(localBlockchain);

        node.updateLocalBlockchain(remoteBlockchain);

        assertEquals(expectedLocalList, node.getBlockchain().getLedger());
    }

    @Test
    void updateTransactionsPool() {
        Set<Transaction> transactionPool = new HashSet<>();
        for (int i = 0; i < 40; i++) {
            if (i % 2 == 0) {
                transactionPool.add(DataTransactionTest.getSampleDataTransaction());
            } else {
                transactionPool.add(CoinTransactionTest.getSampleTransaction());
            }
        }
        node.setTransactionPool(transactionPool);
        Set<Transaction> blockTransactions = new HashSet<>();
        for (Transaction t : transactionPool) {
            if (t instanceof DataTransaction){
                blockTransactions.add(t);
            }
        }
        Block block = new Block(null, new MinerNode(), blockTransactions);
        node.updateTransactionsPool(block);
        transactionPool.removeIf(t -> t instanceof DataTransaction);
        assertNotSame(transactionPool, node.getTransactionPool());
        assertEquals(transactionPool, node.getTransactionPool());
    }
}
