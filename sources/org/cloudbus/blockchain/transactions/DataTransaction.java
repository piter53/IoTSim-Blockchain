package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.Node;
import org.cloudbus.cloudsim.core.CloudSim;

public class DataTransaction extends Transaction {

    private final Object data;

    public DataTransaction(double creationTime, BaseNode senderNode, BaseNode receiverNode, Object data) {
        super(creationTime, senderNode, receiverNode);
        this.data = data;
    }

    public DataTransaction(BaseNode senderNode, BaseNode receiverNode, Object data){
        this(CloudSim.clock(), senderNode, receiverNode, data);
    }

    public Object getData() {
        return data;
    }

}
