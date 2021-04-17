package org.cloudbus.blockchain;

import java.util.LinkedList;

public class Blockchain {
    private LinkedList<Block> ledger;
    private int length;

    Blockchain(){}

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Blockchain)) {
            return false;
        }
        Blockchain blockchain = (Blockchain)o;
        return this.ledger.equals(blockchain.ledger);
    }

    public boolean isSubchainOf(Blockchain blockchain) {
        ledger.
    }
}
