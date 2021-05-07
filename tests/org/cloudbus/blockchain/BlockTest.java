package org.cloudbus.blockchain;

import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.HashSet;

public class BlockTest {

    public static Block generateRandomBlock() {
        return new Block(null, new MinerNode(), new HashSet<Transaction>());
    }

}
