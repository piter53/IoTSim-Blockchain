package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;

import java.util.Random;


public class DataTransactionTest {
    private final static Random random = new Random();
    private static final DataTransaction sampleTransaction = new DataTransaction(random.nextInt(), new Node(), new Node(), new Object(), random.nextInt(), random.nextInt(), 100);
    public static DataTransaction getSampleDataTransaction(){
        return sampleTransaction;
    }

}
