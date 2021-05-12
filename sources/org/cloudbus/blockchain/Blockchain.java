package org.cloudbus.blockchain;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.nodes.BaseNode;

import java.util.ArrayList;

public class Blockchain {

    @Setter @Getter
    private ArrayList<Block> ledger;

    public Blockchain(ArrayList<Block> ledger){
        if (!isLedgerValid(ledger)){
            throw new IllegalArgumentException("This ledger is invalid! Encountered a block that is not linked.");
        }
        this.ledger = ledger;
    }

    public Blockchain(){
        this(new ArrayList<Block>());
    }

    public boolean addBlock(Block block) {
        if (isBlockValid(block)) {
            addBlockwithoutChecking(block);
            return true;
        }
        return false;
    }

    public void addBlockwithoutChecking(Block block) {
        ledger.add(block);

    }

    public boolean isBlockValid(Block block){
        if (getLength() >= 0 && block.getPreviousBlock() != getLastBlock()) {
            return false;
        }
        return Block.isBlockValid(block);
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

    public static boolean isLedgerValid(ArrayList<Block> ledger) {
        for (int i = 0; i < ledger.size()-1; i++) {
            if (ledger.get(i + 1).getPreviousBlock() != ledger.get(i)) {
                return false;
            }
        }
        return true;
    }

    public Block getLastBlock(){
        if (!ledger.isEmpty()) {
            return ledger.get(getLength() - 1);
        } else {
            return null;
        }
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
    public ArrayList<Block> getNMostRecentBlocks(int n, boolean ignoreNonExistent) {
        ArrayList<Block> list = new ArrayList<>();
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
