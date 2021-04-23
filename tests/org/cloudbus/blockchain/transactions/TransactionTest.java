package org.cloudbus.blockchain.transactions;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TransactionTest {

    private static final Random random = new Random();
    public static Set<Transaction> generateRandomTransactionSet(int n) {
        Set<Transaction> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (random.nextBoolean()) {
                set.add(DataTransactionTest.getSampleDataTransaction());
            } else {
                set.add(CoinTransactionTest.getSampleTransaction());
            }
        }
        return set;
    }

}
