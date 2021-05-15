package org.cloudbus.blockchain.consensus;

import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.*;

/**
 * @author Piotr Grela
 */
public class ProofOfWork extends ConsensusAlgorithm {

    private WeightedRandomMiner randomMiner;
    private static ProofOfWork singleInstance = null;

    public ProofOfWork(){
        super();
        transactionComparator = Comparator.comparingDouble(Transaction::getFee).reversed();
    }

    @Override
    public MinerNode pickMiner(Collection<MinerNode> minerNodes) {
        randomMiner = new WeightedRandomMiner();
        for (MinerNode node : minerNodes) {
            if (node.getHashpower() != 0) {
                randomMiner.addEntry(node, node.getHashpower());
            }
            else {
                throw new IllegalArgumentException("pickMiner(): MinerNode's hashpower is 0!");
            }
        }
        return randomMiner.getRandom();
    }

    @Override
    public double calculateTransactionFee(Transaction transaction) {
        double fee = getBaseTransactionFee();
        fee += (double)transaction.getSize() * getFeePerMb();
        return fee;
    }
}

class WeightedRandomMiner {

    private List<Entry> entries = new ArrayList<>();
    private long accumulatedWeight;
    private Random rand = new Random();

    private class Entry {
        long accumulatedWeight;
        MinerNode node;
    }

    public void addEntry(MinerNode object, long weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.node = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public MinerNode getRandom() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.node;
            }
        }
        return null; //should only happen when there are no entries
    }
}
