package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.*;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicy;
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

    default void broadcastThroughBlockchainIfPossible(Object object, int destinationId, int tag) {
        try {
            if (getTransmissionPolicy().canTransmitThroughBlockchain(object) &&
                getNetwork().getConsensusProtocol().getGlobalTransmissionPolicy().canTransmitThroughBlockchain(object)) {
                if (object instanceof BlockchainItem) {
                    broadcastBlockchainItem((BlockchainItem) object);
                } else if (object instanceof Flow) {
                    Flow flow = (Flow) object;
                    BaseNode recipentNode = getNetwork().getNodeByEntityId(flow.getDatacenterId());
                    Transaction transaction = new DataTransaction(CloudSim.clock(), getBlockchainNode(), recipentNode, flow, destinationId, tag, flow.getSize());
                    broadcastBlockchainItem(transaction);
                }
            }
            else {
                sendNow(destinationId, tag, (Flow) object);
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    default void broadcastBlockchainItem(BlockchainItem blockchainItem) {
        int tag = blockchainItem.getBROADCAST_TAG();
        for (BlockchainDevice n : getNetwork().getBlockchainDevicesSet()) {
            if (n != this) {
                sendNow(((SimEntity) n).getId(), tag, blockchainItem);
            }
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
            transaction.processTransaction(this, block.getMiner());
        }
    }

    default boolean appendLocalBlockchain(Block block) {
        return getBlockchainNode().appendLocalBlockchain(block);
    }

    default boolean canTransmitThroughBlockchain(Object o){
        return Transaction.canBeTransmittedThroughBlockchain((Transaction)o) && getTransmissionPolicy().canTransmitThroughBlockchain(o);
    }

    BaseNode getBlockchainNode();

    void sendNow(int id, int tag, Object o);

    TransmissionPolicy getTransmissionPolicy();

    void setBlockchainNode(BaseNode blockchainNode);

    void setTransmissionPolicy(TransmissionPolicy transmissionPolicy);

    void processEvent(SimEvent event);

}
