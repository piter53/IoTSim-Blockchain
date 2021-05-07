package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public class DataTransaction extends Transaction {

    private final Object data;

    private final long size;

    public DataTransaction(double creationTime, BaseNode senderNode, BaseNode receiverNode, Object data, long size, double fee) {
        super(creationTime, senderNode, receiverNode, fee);
        this.data = data;
        this.size = size;
    }

    public DataTransaction(BaseNode senderNode, BaseNode receiverNode, Object data, long size, double fee){
        this(CloudSim.clock(), senderNode, receiverNode, data, size, fee);
    }

    public Object getData() {
        return data;
    }

    public long getSize() {
        return size;
    }

}
