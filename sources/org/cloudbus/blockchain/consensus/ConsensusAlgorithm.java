package org.cloudbus.blockchain.consensus;

import lombok.Getter;
import lombok.Setter;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicy;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicySizeBased;
import org.cloudbus.blockchain.transactions.Transaction;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author Piotr Grela
 */
public abstract class ConsensusAlgorithm {

    @Getter
    private TransmissionPolicy globalTransmissionPolicy;
    @Getter
    private Double blockInterval;
    @Getter
    Comparator<Transaction> transactionComparator;
    @Getter @Setter
    private double blockGenerationReward;
    @Getter
    private double baseTransactionFee;
    @Getter
    private double feePerMb;
    @Getter
    private int baseTransationSize;
    @Getter
    private long maxBlockSize;

    public ConsensusAlgorithm(){
        // TODO read data from file
         globalTransmissionPolicy = new TransmissionPolicySizeBased((long)200);
         blockInterval = 8.0;
         blockGenerationReward = 1;
         baseTransactionFee = 0.01;
         feePerMb = 0.001;
         baseTransationSize = 1;
         maxBlockSize = 10000;
    }

    public abstract double calculateTransactionFee(Transaction transaction);

    abstract public MinerNode pickMiner(Collection<MinerNode> minerNodes);
}
