/*
 * Title:        IoTSim-Osmosis 1.0
 * Description:  IoTSim-Osmosis enables the testing and validation of osmotic computing applications
 * 			     over heterogeneous edge-cloud SDN-aware environments.
 *
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2020, Newcastle University (UK) and Saudi Electronic University (Saudi Arabia)
 *
 */

package org.cloudbus.osmosis.core;


import com.google.gson.Gson;
import org.cloudbus.blockchain.devices.EdgeBlockchainDevice;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.blockchain.policies.TransmissionPolicySizeBased;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDevice;
import org.cloudbus.cloudsim.edge.core.edge.MEL;
import org.cloudbus.cloudsim.edge.core.edge.Mobility;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.CloudDataCenterEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.ControllerEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.EdgeDataCenterEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.EdgeDatacenterCharacteristicsEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.EdgeDeviceEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.IotDeviceEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.LogEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.MELEntities;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.NetworkModelEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.VMEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.VmAllcationPolicyEntity;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity.WanEntity;
import org.cloudbus.cloudsim.edge.core.edge.Mobility.MovingRange;
import org.cloudbus.cloudsim.edge.iot.IoTDevice;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetwork;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetworkInfo;
import org.cloudbus.cloudsim.edge.iot.protocol.AMQPProtocol;
import org.cloudbus.cloudsim.edge.iot.protocol.CoAPProtocol;
import org.cloudbus.cloudsim.edge.iot.protocol.IoTProtocol;
import org.cloudbus.cloudsim.edge.iot.protocol.MQTTProtocol;
import org.cloudbus.cloudsim.edge.iot.protocol.XMPPProtocol;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.edge.utils.LogUtil.Level;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.cloudsim.sdn.example.policies.VmAllocationPolicyCombinedMostFullFirst;
import org.cloudbus.cloudsim.sdn.example.policies.VmSchedulerTimeSharedEnergy;
import org.cloudbus.cloudsim.sdn.power.PowerUtilizationMaxHostInterface;
import org.cloudbus.osmosis.core.polocies.SDNTrafficPolicyFairShare;
import org.cloudbus.osmosis.core.polocies.SDNTrafficSchedulingPolicy;
import org.cloudbus.osmosis.core.polocies.SDNRoutingLoadBalancing;
import org.cloudbus.osmosis.core.polocies.SDNRoutingPolicy;
import org.cloudbus.osmosis.core.polocies.VmMELAllocationPolicyCombinedLeastFullFirst;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 **/

public class OsmosisBuilder {
    public static List<EdgeDataCenter> edgeDatacentres;
    public static int flowId = 1;
    public static int edgeLetId = 1;
    public static int hostId = 1;
    private static int vmId = 1;
    List<CloudDatacenter> cloudDatacentres;
    private OsmesisBroker broker;
    private SDNController sdWanController;
    private List<OsmesisDatacenter> osmesisDatacentres;

    public OsmosisBuilder(OsmesisBroker osmesisBroker) {
        this.broker = osmesisBroker;
        this.osmesisDatacentres = new ArrayList<>();
    }

    public SDNController getSdWanController() {
        return sdWanController;
    }

    public List<EdgeDataCenter> getEdgeDatacentres() {
        return edgeDatacentres;
    }

    public List<CloudDatacenter> getCloudDatacentres() {
        return cloudDatacentres;
    }

    public List<OsmesisDatacenter> getOsmesisDatacentres() {
        return osmesisDatacentres;
    }

    public ConfiguationEntity parseTopology(FileReader jsonFileReader) {
        Gson gson = new Gson();
        ConfiguationEntity conf = gson.fromJson(jsonFileReader, ConfiguationEntity.class);
        return conf;
    }


    /*
     *
     * Create Cloud DataCenters
     *
     */

    protected List<CloudDatacenter> buildCloudDatacentres(List<CloudDataCenterEntity> datacentreEntities) throws Exception {
        List<CloudDatacenter> datacentres = new ArrayList<CloudDatacenter>();

        for (CloudDataCenterEntity datacentreEntity : datacentreEntities) {
            // Assumption: every datacentre only has one controller
            SDNController sdnController = createCloudSDNController(datacentreEntity.getControllers().get(0));
            VmAllocationPolicyFactory vmAllocationPolicyFactory = null;
            if (datacentreEntity.getVmAllocationPolicy().equals("VmAllocationPolicyCombinedFullFirst")) {
                vmAllocationPolicyFactory = hostList -> new VmAllocationPolicyCombinedMostFullFirst();
            }
            if (datacentreEntity.getVmAllocationPolicy().equals("VmAllocationPolicyCombinedLeastFullFirst")) {
                vmAllocationPolicyFactory = hostList -> new VmMELAllocationPolicyCombinedLeastFullFirst();
            }

            CloudDatacenter datacentre = createCloudDatacenter(
                datacentreEntity.getName(),
                sdnController,
                vmAllocationPolicyFactory
            );
            datacentre.initCloudTopology(datacentreEntity.getHosts(), datacentreEntity.getSwitches(),
                datacentreEntity.getLinks());
            datacentre.feedSDNWithTopology(sdnController);
            datacentre.setGateway(datacentre.getSdnController().getGateway());
            datacentre.setDcType(datacentreEntity.getType());
            List<Vm> vmList = createVMs(datacentreEntity.getVMs());

            for (Vm mel : vmList) {
                datacentre.mapVmNameToID(mel.getId(), mel.getVmName());
            }

            this.broker.mapVmNameToId(datacentre.getVmNameToIdList());

            sdnController.setDatacenter(datacentre);
            sdnController.addVmsToSDNhosts(vmList);
            datacentre.getVmAllocationPolicy().setUpVmTopology(datacentre.getHosts());
            datacentre.setVmList(vmList);
            datacentres.add(datacentre);
        }

        return datacentres;
    }

    protected CloudDatacenter createCloudDatacenter(String name, SDNController sdnController,
                                                    VmAllocationPolicyFactory vmAllocationFactory) {

        List<Host> hostList = sdnController.getHostList();
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, time_zone, cost, costPerMem,
            costPerStorage, costPerBw);

        // Create Datacenter with previously set parameters
        CloudDatacenter datacenter = null;
        try {
            VmAllocationPolicy vmPolicy = vmAllocationFactory.create(hostList);
            System.out.println(vmPolicy.getPolicyName());
            if (vmPolicy instanceof VmAllocationPolicyCombinedMostFullFirst) {
                vmPolicy.setPolicyName("CombinedMostFullFirst");
            } else if (vmPolicy instanceof VmMELAllocationPolicyCombinedLeastFullFirst) {
                vmPolicy.setPolicyName("CombinedLeastFullFirst");
            }
            // Why to use maxHostHandler!
            PowerUtilizationMaxHostInterface maxHostHandler = (PowerUtilizationMaxHostInterface) vmPolicy;
            datacenter = new CloudDatacenter(name, characteristics, vmPolicy, storageList, 0, sdnController);

            sdnController.setDatacenter(datacenter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    protected SDNController createCloudSDNController(ControllerEntity controllerEntity) {

        SDNTrafficSchedulingPolicy sdnMapReducePolicy = null;
        SDNRoutingPolicy sdnRoutingPolicy = null;
        String sdnName = controllerEntity.getName();
        switch (controllerEntity.getRoutingPolicy()) {
            case "ShortestPathBw":
                sdnRoutingPolicy = new SDNRoutingLoadBalancing();
                break;
        }

        switch (controllerEntity.getTrafficPolicy()) {
            case "FairShare":
                sdnMapReducePolicy = new SDNTrafficPolicyFairShare();
                break;
        }

        SDNController sdnController = new CloudSDNController(controllerEntity.getName(), sdnMapReducePolicy, sdnRoutingPolicy);
        sdnController.setName(sdnName);
        return sdnController;
    }

    private List<Vm> createVMs(List<VMEntity> vmEntites) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> vmList = new ArrayList<Vm>();

        for (VMEntity vmEntity : vmEntites) {
            //VM Parameters
            int pesNumber = vmEntity.getPes(); //number of cpus
            int mips = (int) vmEntity.getMips();
            int ram = vmEntity.getRam(); //vm memory (MB)
            long size = (long) vmEntity.getStorage(); //image size (MB)
            long bw = vmEntity.getBw();

            String vmm = "Xen"; //VMM name

            //create VMs
            Vm vm = new Vm(vmId, this.broker.getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vm.setVmName(vmEntity.getName());
            vmList.add(vm);
            vmId++;
        }
        return vmList;
    }


    /*
     *
     * Build SD-WAN network
     *
     */

    private SDNController buildWan(List<WanEntity> wanEntity, List<Switch> datacenterGateways) {
        for (WanEntity wan : wanEntity) {
            // Assumption: every datacentre only has one controller            
            sdWanController = createWanController(wan.getControllers());
            sdWanController.initSdWANTopology(wan.getSwitches(), wan.getLinks(), datacenterGateways);
        }
        return sdWanController;
    }

    private void setWanControllerToDatacenters(SDNController wanController, List<OsmesisDatacenter> datacentres) {
        for (OsmesisDatacenter datacenter : datacentres) {
            SDNController controller = datacenter.getSdnController();
            controller.setWanController(wanController);
        }
    }

    protected SDNController createWanController(ControllerEntity controllerEntity) {
        SDNTrafficSchedulingPolicy sdnMapReducePolicy = null;
        SDNRoutingPolicy sdnRoutingPolicy = null;

        String sdnName = controllerEntity.getName();

        switch (controllerEntity.getRoutingPolicy()) {
            case "ShortestPathBw":
                sdnRoutingPolicy = new SDNRoutingLoadBalancing();
                break;
        }

        switch (controllerEntity.getTrafficPolicy()) {
            case "FairShare":
                sdnMapReducePolicy = new SDNTrafficPolicyFairShare();
                break;
        }

        SDNController sdnController = new SDWANController(controllerEntity.getName(), sdnMapReducePolicy, sdnRoutingPolicy);
        sdnController.setName(sdnName);
        return sdnController;
    }

    /*
     *
     * Build Edge Datacenters
     *
     */

    protected SDNController creatEdgeSDNController(ControllerEntity controllerEntity) {
        SDNTrafficSchedulingPolicy sdnMapReducePolicy = null;
        SDNRoutingPolicy sdnRoutingPolicy = null;
        String sdnName = controllerEntity.getName();

        switch (controllerEntity.getRoutingPolicy()) {
            case "ShortestPathBw":
                sdnRoutingPolicy = new SDNRoutingLoadBalancing();
                break;
        }

        switch (controllerEntity.getTrafficPolicy()) {
            case "FairShare":
                sdnMapReducePolicy = new SDNTrafficPolicyFairShare();
                break;
        }

        SDNController sdnController = new EdgeSDNController(controllerEntity.getName(), sdnMapReducePolicy, sdnRoutingPolicy);
        sdnController.setName(sdnName);
        return sdnController;
    }

    private List<MEL> createMEL(List<MELEntities> melEntities, int edgeDatacenterId, OsmesisBroker broker) {
        List<MEL> vms = new ArrayList<>();
        for (MELEntities melEntity : melEntities) {

            String cloudletSchedulerClassName = melEntity.getCloudletSchedulerClassName();
            CloudletScheduler cloudletScheduler;
            try {

                cloudletScheduler = (CloudletScheduler) Class.forName(cloudletSchedulerClassName).newInstance();
                float datasizeShrinkFactor = melEntity.getDatasizeShrinkFactor();
                MEL microELement = new MEL(edgeDatacenterId, vmId, broker.getId(), melEntity.getMips(),
                    melEntity.getPesNumber(), melEntity.getRam(), melEntity.getBw(),
                    melEntity.getVmm(), cloudletScheduler, datasizeShrinkFactor);
                microELement.setVmName(melEntity.getName());
                vms.add(microELement);
                vmId++;
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return vms;
    }
    
    private EdgeNetworkInfo SetEdgeNetworkModel(NetworkModelEntity networkModelEntity) {
        String communicationProtocolName = networkModelEntity.getCommunicationProtocol();
        communicationProtocolName = communicationProtocolName.toLowerCase();
        IoTProtocol communicationProtocol = null;
        switch (communicationProtocolName) {
            case "xmpp":
                communicationProtocol = new XMPPProtocol();
                break;
            case "mqtt":
                communicationProtocol = new MQTTProtocol();
                break;
            case "coap":
                communicationProtocol = new CoAPProtocol();
                break;
            case "amqp":
                communicationProtocol = new AMQPProtocol();
                break;
            default:
                System.out.println("have not supported protocol " + communicationProtocol + " yet!");
                return null;
        }
        String networkTypeName = networkModelEntity.getNetworkType();
        networkTypeName = networkTypeName.toLowerCase();

        EdgeNetwork edgeNetwork = new EdgeNetwork(networkTypeName);
        EdgeNetworkInfo networkModel = new EdgeNetworkInfo(edgeNetwork, communicationProtocol);
        return networkModel;
    }

    private void initLog(ConfiguationEntity conf) {
        LogEntity logEntity = conf.getLogEntity();
        boolean saveLogToFile = logEntity.isSaveLogToFile();
        if (saveLogToFile) {
            String logFilePath = logEntity.getLogFilePath();
            String logLevel = logEntity.getLogLevel();
            boolean append = logEntity.isAppend();
            LogUtil.initLog(Level.valueOf(logLevel.toUpperCase()), logFilePath, saveLogToFile, append);
        }
    }
}
