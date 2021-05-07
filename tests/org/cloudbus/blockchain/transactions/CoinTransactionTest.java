package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;

import java.util.Random;

public class CoinTransactionTest {
    private static final Random random = new Random();
    private static final CoinTransaction sampleTransaction = new CoinTransaction(new Node(), new Node(), random.nextInt(), 0.4);

    public static CoinTransaction getSampleTransaction(){
        return sampleTransaction;
    }
}
