package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.*;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.blockchain.transactions.DataTransaction;
import org.cloudbus.blockchain.transactions.Transaction;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.osmosis.core.Flow;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public interface BlockchainDevice {

    default void broadcast(Object object) {
        if (object instanceof BlockchainItem) {
            broadcastItem((BlockchainItem) object);
        } else if (object instanceof Flow) {
            Flow flow = (Flow) object;
            BaseNode recipentNode = getNetwork().getNodeByEntityId(flow.getDatacenterId());
            Transaction transaction = new DataTransaction(CloudSim.clock(), getBlockchainNode(), recipentNode, flow, flow.getSize(), Transaction.calculateTransactionFee(flow.getSize()));
            broadcastItem(transaction);
        }
    }

    default void broadcastItem(BlockchainItem transaction) {
        for (BlockchainDevice n : getNetwork().getBlockchainDevicesSet()) {
            int tag = -1;
            if (transaction instanceof Transaction) {
                tag = BlockchainTags.BROADCAST_TRANSACTION;
            } else if (transaction instanceof Block) {
                tag = BlockchainTags.BROADCAST_BLOCK;
            }
            sendNow(((SimEntity) n).getId(), tag, transaction);
        }
    }

    default Network getNetwork(){
        return Network.getInstance();
    }

    default void appendTransactionPool(Transaction transaction) {
        getBlockchainNode().appendTransactionsPool(transaction);
    }

    default boolean canTransmitThroughBlockchain(Object o){
        return Transaction.canTransmitThroughBlockchain(o) && getTransmissionPolicy().canTransmitThroughBlockchain(o);
    }

    BaseNode getBlockchainNode();

    void sendNow(int id, int tag, Object o);

    TransmissionPolicy getTransmissionPolicy();

    void setBlockchainNode(BaseNode blockchainNode);

    void setTransmissionPolicy(TransmissionPolicy transmissionPolicy);

    void processEvent(SimEvent event);

}
