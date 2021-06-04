package org.cloudbus.blockchain.consensus.policies;

import org.cloudbus.blockchain.transactions.Transaction;
import org.cloudbus.osmosis.core.Flow;

public class TransmissionPolicySizeBased implements TransmissionPolicy{

    private long maxSize;

    public TransmissionPolicySizeBased(Long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean canTransmitThroughBlockchain(Object o) {
        if (o instanceof Flow) {
            return ((Flow) o).getAmountToBeProcessed() <= maxSize;
        } else if (o instanceof Transaction) {
            return ((Transaction)o).getSizeMB() <= maxSize;
        }
        return false;
    }

}
