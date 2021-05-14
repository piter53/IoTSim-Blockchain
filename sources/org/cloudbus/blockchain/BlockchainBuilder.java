package org.cloudbus.blockchain;

import org.cloudbus.blockchain.devices.EdgeBlockchainDevice;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicy;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicySizeBased;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.edge.core.edge.*;
import org.cloudbus.cloudsim.edge.iot.IoTDevice;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetworkInfo;
import org.cloudbus.cloudsim.provisioners.*;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.cloudsim.sdn.example.policies.VmSchedulerTimeSharedEnergy;
import org.cloudbus.osmosis.core.OsmesisBroker;
import org.cloudbus.osmosis.core.OsmesisDatacenter;
import org.cloudbus.osmosis.core.OsmosisBuilder;
import org.cloudbus.osmosis.core.SDNController;
import org.cloudbus.osmosis.core.polocies.VmMELAllocationPolicyCombinedLeastFullFirst;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Piotr Grela
 */
public class BlockchainBuilder extends OsmosisBuilder {

    private static Network blockchainNetwork = Network.getInstance();

    public BlockchainBuilder(OsmesisBroker osmesisBroker) {
        super(osmesisBroker);
    }

    @Override
    public List<EdgeDataCenter> buildEdgeDatacentres(List<ConfiguationEntity.EdgeDataCenterEntity> edgeDatacenerEntites) {
        List<EdgeDataCenter> edgeDC = new ArrayList<EdgeDataCenter>();
        for (ConfiguationEntity.EdgeDataCenterEntity edgeDCEntity : edgeDatacenerEntites) {
            List<ConfiguationEntity.EdgeDeviceEntity> hostListEntities = edgeDCEntity.getHosts();
            List<EdgeDevice> hostList = new ArrayList<EdgeDevice>();
            try {
                for (ConfiguationEntity.EdgeDeviceEntity hostEntity : hostListEntities) {
                    ConfiguationEntity.VmAllcationPolicyEntity vmSchedulerEntity = edgeDCEntity.getVmAllocationPolicy();
                    String vmSchedulerClassName = vmSchedulerEntity.getClassName();
                    LinkedList<Pe> peList = new LinkedList<Pe>();
                    int peId = 0;
                    for (int i = 0; i < hostEntity.getPes(); i++) {
                        peList.add(new Pe(peId++, new PeProvisionerSimple(hostEntity.getMips())));
                    }

                    RamProvisioner ramProvisioner = new RamProvisionerSimple(hostEntity.getRamSize());
                    BwProvisioner bwProvisioner = new BwProvisionerSimple(hostEntity.getBwSize());
                    VmScheduler vmScheduler = new VmSchedulerTimeSharedEnergy(peList);

                    EdgeDevice edgeDevice = new EdgeDevice(hostId, hostEntity.getName(), ramProvisioner, bwProvisioner,
                        hostEntity.getStorage(), peList, vmScheduler);

                    hostList.add(edgeDevice);
                    hostId++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ConfiguationEntity.EdgeDatacenterCharacteristicsEntity characteristicsEntity = edgeDCEntity.getCharacteristics();
            String architecture = characteristicsEntity.getArchitecture();
            String os = characteristicsEntity.getOs();
            String vmm = characteristicsEntity.getVmm();
            double timeZone = characteristicsEntity.getTimeZone();
            double costPerMem = characteristicsEntity.getCostPerMem();
            double cost = characteristicsEntity.getCost();

            double costPerStorage = characteristicsEntity.getCostPerStorage();
            double costPerBw = characteristicsEntity.getCostPerBw();
            LinkedList<Storage> storageList = new LinkedList<Storage>();


            DatacenterCharacteristics characteristics = new DatacenterCharacteristics(architecture, os, vmm,
                hostList, timeZone, cost, costPerMem, costPerStorage, costPerBw);

            ConfiguationEntity.VmAllcationPolicyEntity vmAllcationPolicyEntity = edgeDCEntity.getVmAllocationPolicy();
            String className = vmAllcationPolicyEntity.getClassName();

            // 6. Finally, we need to create a PowerDatacenter object.
            EdgeDataCenter datacenter = null;
            VmAllocationPolicy vmAllocationPolicy = null;
            String deviceClassName = edgeDCEntity.getClassName();
            try {
                switch (className) {
                    case "VmAllocationPolicyCombinedLeastFullFirst":
                        vmAllocationPolicy = new VmMELAllocationPolicyCombinedLeastFullFirst();
                        break;
                }
                Class<?> aClass = Class.forName(deviceClassName);
                if (!EdgeDataCenter.class.isAssignableFrom(aClass)) {
                    System.out.println("Class " + aClass + " is not a correct EdgeDataCenter class");
                    return null;
                }
                Constructor<?> constructor = aClass.getConstructor(String.class, DatacenterCharacteristics.class, VmAllocationPolicy.class, List.class, Double.class);
                datacenter = (EdgeDataCenter) constructor.newInstance(edgeDCEntity.getName(), characteristics, vmAllocationPolicy, storageList,
                    edgeDCEntity.getSchedulingInterval());
                if (datacenter instanceof EdgeBlockchainDevice) {
                    ConfiguationEntity.EdgeBlockchainDeviceEntity blockchainEntity = (ConfiguationEntity.EdgeBlockchainDeviceEntity) edgeDCEntity;
                    BaseNode node = createBaseNode(blockchainEntity.getBaseNodeEntity());
                    TransmissionPolicy policy = createTransmissionPolicy(blockchainEntity.getTransmissionPolicy());
                    ((EdgeBlockchainDevice) datacenter).setBlockchainNode(node);
                    ((EdgeBlockchainDevice) datacenter).setTransmissionPolicy(policy);
                }
                datacenter.setDcType(edgeDCEntity.getType());

            } catch (Exception e) {
                e.printStackTrace();
            }

            for (ConfiguationEntity.ControllerEntity controller : edgeDCEntity.getControllers()) {
                SDNController edgeSDNController = creatEdgeSDNController(controller);
                datacenter.setSdnController(edgeSDNController);
                edgeSDNController.setDatacenter(datacenter);
            }
            System.out.println("Edge SDN cotroller has been created");
            datacenter.initEdgeTopology(hostList, edgeDCEntity.getSwitches(), edgeDCEntity.getLinks());
            datacenter.feedSDNWithTopology(datacenter.getSdnController());

            datacenter.setGateway(datacenter.getSdnController().getGateway());

            List<MEL> MELList = createMEL(edgeDCEntity.getMELEntities(), datacenter.getId(), this.getBroker());
            datacenter.setVmList(MELList);

            for (MEL mel : MELList) {
                datacenter.mapVmNameToID(mel.getId(), mel.getVmName());
            }

            this.getBroker().mapVmNameToId(datacenter.getVmNameToIdList());
            datacenter.getVmAllocationPolicy().setUpVmTopology(hostList);
            datacenter.getSdnController().addVmsToSDNhosts(MELList);

            List<IoTDevice> devices = createIoTDevice(edgeDCEntity.getIoTDevices());
            this.getBroker().setIoTDevices(devices);
            edgeDC.add(datacenter);
            if (datacenter instanceof EdgeBlockchainDevice) {
                blockchainNetwork.addEdgeBlockchainDevice((EdgeBlockchainDevice) datacenter);
                List<ConfiguationEntity.IotDeviceEntity> entities = new ArrayList<>(edgeDCEntity.getIoTBlockchainDevices());
                devices = createIoTDevice(entities);
                this.getBroker().setIoTDevices(devices);
            }

        }
        return edgeDC;
    }


    @Override
    public void buildTopology(ConfiguationEntity topologyEntity) throws Exception {
        List<ConfiguationEntity.CloudDataCenterEntity> datacentreEntities = topologyEntity.getCloudDatacenter();
        this.setCloudDatacentres(buildCloudDatacentres(datacentreEntities));
        getOsmesisDatacentres().addAll(this.getCloudDatacentres());

        List<ConfiguationEntity.EdgeDataCenterEntity> edgeDatacenerEntites = topologyEntity.getEdgeDatacenter();
        if (edgeDatacenerEntites != null && !edgeDatacenerEntites.isEmpty()) {
            edgeDatacentres = buildEdgeDatacentres(edgeDatacenerEntites);
        }
        List<ConfiguationEntity.EdgeDataCenterEntity> edgeBlockchainDatacenterEntities = new ArrayList<>(topologyEntity.getEdgeBlockchainDatacentre());
        if (!edgeBlockchainDatacenterEntities.isEmpty()) {
            edgeDatacentres = new ArrayList<>();
            edgeDatacentres.addAll(buildEdgeDatacentres(edgeBlockchainDatacenterEntities));
        }
        getOsmesisDatacentres().addAll(edgeDatacentres);



        initLog(topologyEntity);
        List<Switch> datacenterGateways = new ArrayList<>();

        for (OsmesisDatacenter osmesisDC : this.getOsmesisDatacentres()) {
            datacenterGateways.add(osmesisDC.getSdnController().getGateway());

        }
        List<ConfiguationEntity.WanEntity> wanEntities = topologyEntity.getSdwan();
        this.setSdWanController(buildWan(wanEntities, datacenterGateways));
        setWanControllerToDatacenters(getSdWanController(), getOsmesisDatacentres());
        getSdWanController().addAllDatacenters(getOsmesisDatacentres());
    }

    @Override
    public List<IoTDevice> createIoTDevice(List<ConfiguationEntity.IotDeviceEntity> iotDeviceEntityList) {
        List<IoTDevice> devices = new ArrayList<>();
        for (ConfiguationEntity.IotDeviceEntity iotDevice : iotDeviceEntityList) {
            String ioTClassName = iotDevice.getIoTClassName();
            ConfiguationEntity.NetworkModelEntity networkModelEntity = iotDevice.getNetworkModelEntity();
            // xmpp, mqtt, coap, amqp
            EdgeNetworkInfo networkModel = this.SetEdgeNetworkModel(networkModelEntity);
            try {
                Class<?> clazz = Class.forName(ioTClassName);
                if (!IoTDevice.class.isAssignableFrom(clazz)) {
                    System.out.println("this class is not correct type of ioT Device");
                    return null;
                }
                Constructor<?> constructor = clazz.getConstructor(EdgeNetworkInfo.class, String.class, double.class);

                IoTDevice newInstance = (IoTDevice) constructor.newInstance(networkModel, iotDevice.getName(), iotDevice.getBw());
                newInstance.getBattery().setMaxCapacity(iotDevice.getMax_battery_capacity());
                newInstance.getBattery().setCurrentCapacity(iotDevice.getMax_battery_capacity());
                newInstance.getBattery().setBatterySensingRate(iotDevice.getBattery_sensing_rate());
                newInstance.getBattery().setBatterySendingRate(iotDevice.getBattery_sending_rate());

                Mobility location = new Mobility(iotDevice.getMobilityEntity().getLocation());
                location.movable = iotDevice.getMobilityEntity().isMovable();
                if (iotDevice.getMobilityEntity().isMovable()) {
                    location.range = new Mobility.MovingRange(iotDevice.getMobilityEntity().getRange().beginX,
                        iotDevice.getMobilityEntity().getRange().endX);
                    location.signalRange = iotDevice.getMobilityEntity().getSignalRange();
                    location.velocity = iotDevice.getMobilityEntity().getVelocity();
                }
                newInstance.setMobility(location);
                if (newInstance instanceof IoTBlockchainDevice) {
                    IoTBlockchainDevice blockchainDevice = (IoTBlockchainDevice)newInstance;
                    ConfiguationEntity.IoTBlockchainDeviceEntity blockchainDeviceEntity = (ConfiguationEntity.IoTBlockchainDeviceEntity)iotDevice;
                    blockchainDevice.setBlockchainNode(createBaseNode(blockchainDeviceEntity.getBaseNodeEntity()));
                    blockchainDevice.setTransmissionPolicy(createTransmissionPolicy(blockchainDeviceEntity.getTransmissionPolicy()));
                    blockchainNetwork.addIoTBlockchainDevice(blockchainDevice);
                    newInstance = blockchainDevice;
                }

                devices.add(newInstance);

            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return devices;
    }

    /**
     * Create BaseNode object based on BaseNodeEntity passed as a parameter.
     * Throws an exception if instantiation of class name is not possible
     *
     * @param baseNodeEntity
     * @return
     * @author Piotr Grela
     */
    private BaseNode createBaseNode(ConfiguationEntity.BaseNodeEntity baseNodeEntity) {
        try {
            Class<?> aClass = Class.forName(baseNodeEntity.getClassName());
            if (!BaseNode.class.isAssignableFrom(aClass)) {
                System.out.println("Class \"" + aClass + "\" is not a correct BaseNode");
                return null;
            }
            Constructor<?> constructor;
            if (baseNodeEntity.getBlockchainDepth() != 0) {
                if (baseNodeEntity.getHashpower() != 0) {
                    constructor = aClass.getConstructor(Integer.class, Long.class);
                    return (BaseNode) constructor.newInstance(baseNodeEntity.getBlockchainDepth(), baseNodeEntity.getHashpower());
                }
                else {
                    constructor = aClass.getConstructor(Integer.class);
                    return (BaseNode) constructor.newInstance(baseNodeEntity.getBlockchainDepth());
                }
            } else {
                constructor = aClass.getConstructor();
                return (BaseNode) constructor.newInstance();
            }
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                e.getCause();
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates TransmissionPolicy based on TransmissionPolicyEntity.
     * Throws an exception should class instantiation based on given name not be possible.
     * Currently, function can create a TransmissionPolicy object with constructor that requires an Object as a parameter.
     *
     * @param transmissionPolicyEntity
     * @return
     * @author Piotr Grela
     */
    private TransmissionPolicy createTransmissionPolicy(ConfiguationEntity.TransmissionPolicyEntity transmissionPolicyEntity) {
        try {
            Class<?> aClass = Class.forName(transmissionPolicyEntity.getClassName());
            if (!TransmissionPolicy.class.isAssignableFrom(aClass)) {
                System.out.println("Class \"" + aClass + "\" is not a correct type of TransmissionPolicy");
                return null;
            }
            Constructor<?> constructor;
            if (TransmissionPolicySizeBased.class.isAssignableFrom(aClass)) {
                constructor = aClass.getConstructor(Long.class);
                return (TransmissionPolicy) constructor.newInstance(((Double) transmissionPolicyEntity.getObject()).longValue());
            }
            constructor = aClass.getConstructor(Object.class);
            return (TransmissionPolicy) constructor.newInstance(transmissionPolicyEntity.getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
