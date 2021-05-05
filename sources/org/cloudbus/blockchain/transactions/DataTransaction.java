package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;

public class DataTransaction extends Transaction {

    private final Object edgeLet;

    public DataTransaction(double creationTime, Node senderNode, Node receiverNode, Object edgeLet) {
        super(creationTime, senderNode, receiverNode);
        this.edgeLet = edgeLet;
    }

    public DataTransaction(Node senderNode, Node receiverNode, Object edgeLet){
        this(CloudSim.clock(), senderNode, receiverNode, edgeLet);
    }

    public Object getEdgeLet() {
        return edgeLet;
    }


}
