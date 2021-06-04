package org.cloudbus.blockchain.consensus;

import org.cloudbus.blockchain.consensus.policies.TransmissionPolicy;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.*;

/**
 * @author Piotr Grela
 */
public class ProofOfWork extends ConsensusProtocol {

    private WeightedRandomMiner randomMiner;
    private static ProofOfWork singleInstance = null;

    public ProofOfWork(TransmissionPolicy transmissionPolicy, double blockInterval, double blockGenerationReward, int baseTransactionSize, double feePerMb, double baseTransactionFee, long maxBlockSize){
        super(transmissionPolicy, blockInterval, blockGenerationReward, baseTransactionSize, feePerMb, baseTransactionFee, maxBlockSize);
        transactionComparator = Comparator.comparingDouble(Transaction::getFee).reversed();
//        transactionComparator = (t1, t2) -> {
//            double t1fee = t1.getFee();
//            double t2fee = t2.getFee();
//            if (t1fee != t2fee) {
//                return Double.compare(t2fee,t1fee);
//            } else {
//                return Double.compare(t1.getCreationTimestamp(),t2.getCreationTimestamp());
//            }
//        };
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
        fee += (double)transaction.getSizeMB() * getFeePerMb();
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
