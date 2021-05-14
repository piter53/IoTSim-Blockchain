package org.cloudbus.blockchain.consensus.policies;

import org.cloudbus.blockchain.transactions.DataTransaction;
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
            if (o instanceof DataTransaction) {
                return ((DataTransaction) o).getSize() <= maxSize;
            } else {
                return true;
            }
        }
        // TODO different object types

        return false;
    }

}
