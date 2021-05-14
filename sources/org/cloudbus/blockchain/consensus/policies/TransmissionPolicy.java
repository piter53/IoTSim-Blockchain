package org.cloudbus.blockchain.consensus.policies;

public interface TransmissionPolicy {

    public boolean canTransmitThroughBlockchain(Object object);
}
