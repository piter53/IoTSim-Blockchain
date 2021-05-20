package org.cloudbus.blockchain.transactions;

import lombok.Getter;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.osmosis.core.Flow;
import org.cloudbus.osmosis.core.OsmosisTags;

/**
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public class DataTransaction extends Transaction {

    @Getter
    private final Object data;
    @Getter
    private final int recipentID;
    @Getter
    private final int tag;

    public DataTransaction(double creationTime, BaseNode senderNode, BaseNode receiverNode, Object data, int recipentID, int tag, long size) {
        super(creationTime, senderNode, receiverNode);
        this.data = data;
        this.size += size;
        this.recipentID = recipentID;
        this.tag = tag;
        this.setFee(getConsensus().calculateTransactionFee(this));
    }

    public DataTransaction(BaseNode senderNode, BaseNode receiverNode, Object data, int recipentID, int tag, long size){
        this(CloudSim.clock(), senderNode, receiverNode, data, recipentID, tag, size);
    }

    @Override
    public void processTransaction(BlockchainDevice device, MinerNode miner) {
        super.processTransaction(device, miner);
        if (getRecipentNode() == device.getBlockchainNode()) {
            Flow flow = (Flow) getData();
            device.sendNow(recipentID, tag, flow);
        }
    }
}
