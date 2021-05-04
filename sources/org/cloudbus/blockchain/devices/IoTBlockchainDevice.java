package org.cloudbus.blockchain.devices;

import org.cloudbus.blockchain.BlockchainTags;
import org.cloudbus.blockchain.network.Network;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.edge.iot.IoTDevice;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetworkInfo;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.osmosis.core.*;

/**
 * Representation of IoTDevice deployed as a given blockchain node type, functioning under given TransmissionPolicy
 * @author Piotr Grela
 * @since IoTSim-Blockchain 1.0
 */
public abstract class IoTBlockchainDevice extends IoTDevice implements BlockchainDevice{

    private BaseNode blockchainNode;
    private TransmissionPolicy transmissionPolicy;
    private static Network blockchainNetwork = Network.getInstance();

    public IoTBlockchainDevice(String name, EdgeNetworkInfo networkModel, double bandwidth, BaseNode node, TransmissionPolicy transmissionPolicy) {
        super(name, networkModel, bandwidth);
        this.blockchainNode = node;
        this.transmissionPolicy = transmissionPolicy;
    }

    @Override
    protected void sensing(SimEvent event) {
        OsmesisAppDescription app = (OsmesisAppDescription) event.getData();

        // if the battery is drained,
        this.updateBatteryBySensing();
        boolean died = this.updateBatteryByTransmission();
        app.setIoTBatteryConsumption(this.battery.getBatteryTotalConsumption());
        if (died) {
            app.setIoTDeviceDied(true);
            LogUtil.info(this.getClass().getSimpleName() + " running time is " + CloudSim.clock());

            setEnabled(false);
            LogUtil.info(this.getClass().getSimpleName()+" " + this.getId() + "'s battery has been drained");
            setRunningTime(CloudSim.clock());
            return;
        }

        Flow flow = createFlow(app);

        WorkflowInfo workflowTag = new WorkflowInfo();
        workflowTag.setStartTime(CloudSim.clock());
        workflowTag.setAppId(app.getAppID());
        workflowTag.setAppName(app.getAppName());
        workflowTag.setIotDeviceFlow(flow);
        workflowTag.setWorkflowId(app.addWorkflowId(1));
        workflowTag.setSourceDCName(app.getEdgeDatacenterName());
        workflowTag.setDestinationDCName(app.getCloudDatacenterName());
        flow.setWorkflowTag(workflowTag);
        OsmesisBroker.workflowTag.add(workflowTag);
        flow.addPacketSize(app.getIoTDeviceOutputSize());
        updateBandwidth();

//        if (transmissionPolicy.canTransmitThroughBlockchain(flow)) {
//            broadcastTransaction(flow, BlockchainTags.BROADCAST_TRANSACTION);
//        }
//        else {
            sendNow(flow.getDatacenterId(), OsmosisTags.TRANSMIT_IOT_DATA, flow);
//        }
    }

    @Override
    public void broadcastTransaction(Flow flow, int tag) {
        for (BlockchainDevice n : blockchainNetwork.getBlockchainDevicesSet()) {
            sendNow(((SimEntity)n).getId(), tag, flow);
        }
    }

    @Override
    public void setBlockchainNode(BaseNode blockchainNode) {
        this.blockchainNode = blockchainNode;
    }

    @Override
    public void setTransmissionPolicy(TransmissionPolicy transmissionPolicy) {
        this.transmissionPolicy = transmissionPolicy;
    }

}
