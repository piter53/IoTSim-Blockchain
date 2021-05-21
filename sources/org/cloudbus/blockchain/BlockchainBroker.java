package org.cloudbus.blockchain;

import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.nodes.MinerNode;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.osmosis.core.OsmesisAppDescription;
import org.cloudbus.osmosis.core.OsmesisBroker;

/**
 * @author Piotr Grela
 */
public class BlockchainBroker extends OsmesisBroker {

    private static Network blockchainNetwork = Network.getInstance();

    public BlockchainBroker(String name) {
        super(name);
    }

    @Override
    public void processEvent(SimEvent ev) {
        int tag = ev.getTag();
        switch (tag) {
            case BlockchainTags.GENERATE_BLOCK: {
                generateBlock();
            }
            default: {
                super.processEvent(ev);
            }
        }
    }

    public void generateBlock() {
        boolean sensingFinished = true;
        for (OsmesisAppDescription app : getAppList()) {
            if (!app.getIsIoTDeviceDied() && app.getStopDataGenerationTime() > CloudSim.clock()) {
                sensingFinished = false;
                break;
            }
        }
        if (blockchainNetwork.existPendingTransactions() || !sensingFinished) {
            BlockchainDevice device = blockchainNetwork.pickNewMiningDevice();
            ((MinerNode) device.getBlockchainNode()).mineBlock(device);
            double blockGenerationDelay = blockchainNetwork.getConsensusProtocol().getBlockInterval();
            send(this.getId(), blockGenerationDelay, BlockchainTags.GENERATE_BLOCK, null);
        }
    }

    @Override
    protected void processResourceCharacteristicsRequest(SimEvent ev) {
        super.processResourceCharacteristicsRequest(ev);
        double blockGenerationDelay = blockchainNetwork.getConsensusProtocol().getBlockInterval();
        send(this.getId(), blockGenerationDelay, BlockchainTags.GENERATE_BLOCK, null);
    }

}
