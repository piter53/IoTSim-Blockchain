package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CoinTransactionTest {
    private static final Random random = new Random();
    private static final CoinTransaction sampleTransaction = new CoinTransaction(new Node(), new Node(), random.nextInt());

    public static CoinTransaction getSampleTransaction(){
        return sampleTransaction;
    }
}
