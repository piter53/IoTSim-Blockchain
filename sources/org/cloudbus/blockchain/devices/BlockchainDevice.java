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

    default void broadcastObject(Object object) {
        if (object instanceof BlockchainItem) {
            broadcastBlockchainItem((BlockchainItem) object);
        } else if (object instanceof Flow) {
            Flow flow = (Flow) object;
            BaseNode recipentNode = getNetwork().getNodeByEntityId(flow.getDatacenterId());
            Transaction transaction = new DataTransaction(CloudSim.clock(), getBlockchainNode(), recipentNode, flow, flow.getSize(), Transaction.calculateTransactionFee(flow.getSize()));
            broadcastBlockchainItem(transaction);
        }
    }

    default void broadcastBlockchainItem(BlockchainItem blockchainItem) {
        int tag = -1;
        if (blockchainItem instanceof Transaction) {
            tag = BlockchainTags.BROADCAST_TRANSACTION;
        } else if (blockchainItem instanceof Block) {
            tag = BlockchainTags.BROADCAST_BLOCK;
        }
        for (BlockchainDevice n : getNetwork().getBlockchainDevicesSet()) {
            sendNow(((SimEntity) n).getId(), tag, blockchainItem);
        }
    }

    default Network getNetwork(){
        return Network.getInstance();
    }

    default boolean processBlockchainEvent(SimEvent event) {
        int tag = event.getTag();
        switch (tag) {
            case BlockchainTags.BROADCAST_TRANSACTION: {
                appendTransactionPool((Transaction) event.getData());
                break;
            }
            case BlockchainTags.BROADCAST_BLOCK: {
                Block block = (Block) event.getData();
                if (appendLocalBlockchain(block)){
                    processAcceptedTransactions(block);
                }
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    default void appendTransactionPool(Transaction transaction) {
        getBlockchainNode().appendTransactionsPool(transaction);
    }

    default void processAcceptedTransactions(Block block){
        for (Transaction transaction : block.getTransactionList()) {
            if (transaction.getRecipentNode() == this.getBlockchainNode()){
                if (transaction instanceof DataTransaction) {
                    SimEvent event = (SimEvent)((DataTransaction) transaction).getData();
                    processBlockchainEvent(event);
                }
            }
        }
    }

    default boolean appendLocalBlockchain(Block block) {
        return getBlockchainNode().appendLocalBlockchain(block);
    }

    default boolean canTransmitThroughBlockchain(Object o){
        return Transaction.canBeTransmittedThroughBlockchain(o) && getTransmissionPolicy().canTransmitThroughBlockchain(o);
    }

    BaseNode getBlockchainNode();

    void sendNow(int id, int tag, Object o);

    TransmissionPolicy getTransmissionPolicy();

    void setBlockchainNode(BaseNode blockchainNode);

    void setTransmissionPolicy(TransmissionPolicy transmissionPolicy);

    void processEvent(SimEvent event);

}
