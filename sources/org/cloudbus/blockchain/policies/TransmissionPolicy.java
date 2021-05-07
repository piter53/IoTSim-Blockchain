package org.cloudbus.blockchain.policies;

public interface TransmissionPolicy {

    public boolean canTransmitThroughBlockchain(Object object);
}
