package org.cloudbus.blockchain;

import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.transactions.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import static org.cloudbus.blockchain.Blockchain.isLedgerValid;
import static org.junit.jupiter.api.Assertions.*;

public class BlockchainTest {

    Blockchain blockchain;

    @BeforeEach
    void setUp(){
        blockchain = new Blockchain();
    }

    @Test
    void testIsLedgerValid(){
        blockchain = BlockchainTest.generateBlockchainWithNLinkedBlocks(20);
        assertTrue(isLedgerValid(blockchain.getLedger()));
        blockchain.getLedger().add(new Block(null, new MinerNode(), new HashSet<Transaction>()));
        assertFalse(isLedgerValid(blockchain.getLedger()));
    }

    public static Blockchain generateBlockchainWithNLinkedBlocks(int n) {
        Blockchain blockchain = new Blockchain();
        Block previousBlock = null;
        Block block;
        for (int i = 0; i < n; i++) {
            block = new Block(previousBlock, new MinerNode(), new HashSet<>());
            blockchain.addBlock(block);
            previousBlock = block;
        }
        return blockchain;
    }

    @Test
    void testGenerateBlockchainWithNLinkedBlocks() {
        for (int i = 10; i < 100; i+=3) {
            assertEquals(i, generateBlockchainWithNLinkedBlocks(i).getLength());
        }
    }

    @Test
    void testEquals() {
        Blockchain blockchain1 = new Blockchain();
        Block block = new Block(null, new MinerNode(), new HashSet<>());
        blockchain1.addBlock(block);
        assertNotEquals(blockchain, blockchain1);
        blockchain.addBlock(block);
        assertEquals(blockchain, blockchain1);
    }

    @Test
    void testGetLastBlock() {
        Block block = new Block(null, new MinerNode(), new HashSet<>());
        Block block1 = new Block(block, new MinerNode(), new HashSet<>());
        blockchain.addBlock(block);
        blockchain.addBlock(block1);
        assertEquals(blockchain.getLastBlock(), block1);
    }

    @Test
    void testAddBlock(){
        Block block = new Block(null, new MinerNode(), new HashSet<>());
        final Block block1 = new Block(null, new MinerNode(), new HashSet<>());
        blockchain.addBlock(block);
        Exception exception = assertThrows(Exception.class, () -> blockchain.addBlock(new Block(null, new MinerNode(), new HashSet<>())));
        blockchain = new Blockchain();
        blockchain.addBlock(block);
        assertDoesNotThrow(() -> blockchain.addBlock(new Block(block, new MinerNode(), new HashSet<>())));

    }

    @Test
    void testGetNMostRecentBlocks() {
        blockchain = generateBlockchainWithNLinkedBlocks(30);
        ArrayList<Block> chain = new ArrayList<>();
        for (int i = 9; i >= 0; i--) {
            chain.add(blockchain.getLedger().get(blockchain.getLength() - 1 - i));
        }
        Blockchain blockchain1 = new Blockchain(chain);
        assertEquals(blockchain1.getLedger(), blockchain.getNMostRecentBlocks(10, true));
        blockchain = generateBlockchainWithNLinkedBlocks(30);
        assertThrows(Exception.class, () -> blockchain1.getNMostRecentBlocks(40, false));
    }
}
