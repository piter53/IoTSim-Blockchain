package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public class DataTransaction extends Transaction {

    private final Object data;

    public DataTransaction(double creationTime, BaseNode senderNode, BaseNode receiverNode, Object data, long size) {
        super(creationTime, senderNode, receiverNode);
        this.data = data;
        this.size += size;
        this.setFee(getConsensus().calculateTransactionFee(this));
    }

    public DataTransaction(BaseNode senderNode, BaseNode receiverNode, Object data, long size){
        this(CloudSim.clock(), senderNode, receiverNode, data, size);
    }

    public Object getData() {
        return data;
    }

    public long getSize() {
        return size;
    }

}
