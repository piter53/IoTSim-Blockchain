package org.cloudbus.blockchain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Blockchain {
    private ArrayList<Block> ledger;

    public Blockchain(ArrayList<Block> ledger){
        this.ledger = ledger;
    }

    public Blockchain(){
        ledger = new ArrayList<>();
    }

    public void addBlock(Block block) throws IllegalArgumentException {
        if (getLength() > 0) {
            if (block.getPreviousBlock() == getLastBlock()) {
                ledger.add(block);
            } else {
                throw new IllegalArgumentException("Passed block's previous block is not the same as last block in the ledger");
            }
        } else {
            ledger.add(block);
        }
    }

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

    public Block getLastBlock(){
        return ledger.get(getLength()-1);
    }

    public ArrayList<Block> getLedger() {
        return ledger;
    }

    public int getLength() {
        return ledger.size();
    }

    /**
     * This method is to return N most recent blocks.
     * @param n
     * @param ignoreNonExistent indicates whether non-existent blocks should be
     * ignored, i.e. when target blockchain's length < n. If false, throw an
     * exception.
     * @return
     */
    public LinkedList<Block> getNMostRecentBlocks(int n, boolean ignoreNonExistent) {
        LinkedList<Block> list = new LinkedList<>();
        for (int i = n; i > 0; i--) {
            try {
                list.add(ledger.get(getLength()-i));
                }
            catch (IndexOutOfBoundsException e){
                if (!ignoreNonExistent) {
                    throw new IndexOutOfBoundsException("Failed to fetch element " + (i+1) + " blocks deep");
                }
            }
        }
        return list;
    }

}
