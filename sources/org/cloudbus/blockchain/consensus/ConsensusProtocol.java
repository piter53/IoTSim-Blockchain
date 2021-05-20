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
public abstract class ConsensusProtocol {

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

    public ConsensusProtocol(TransmissionPolicy transmissionPolicy, double blockInterval, double blockGenerationReward, int baseTransactionSize, double feePerMb, double baseTransactionFee, long maxBlockSize){
         this.globalTransmissionPolicy = transmissionPolicy;
         this.blockInterval = blockInterval;
         this.blockGenerationReward = blockGenerationReward;
         this.baseTransactionFee = baseTransactionFee;
         this.feePerMb = feePerMb;
         this.baseTransationSize = baseTransactionSize;
         this.maxBlockSize = maxBlockSize;
    }

    public abstract double calculateTransactionFee(Transaction transaction);

    abstract public MinerNode pickMiner(Collection<MinerNode> minerNodes);
}
