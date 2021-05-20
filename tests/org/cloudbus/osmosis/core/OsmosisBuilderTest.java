package org.cloudbus.osmosis.core;

import org.cloudbus.cloudsim.edge.core.edge.ConfigurationEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class OsmosisBuilderTest {

    static final String configurationFile = "inputFiles/OsmosisExample1_configuration.json";
    static final String osmesisAppFile =  "inputFiles/Example1_Workload.csv";
    static OsmosisBuilder topologyBuilder;
    static OsmesisBroker osmesisBroker;

    @BeforeAll
    static void setUp () {
        osmesisBroker = new OsmesisBroker("OsmesisBroker");
        topologyBuilder = new OsmosisBuilder(osmesisBroker);
        ConfigurationEntity config = buildTopologyFromFile(configurationFile);
        if (config != null) {
            try {
                topologyBuilder.buildTopology(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("ConfiguationEntity object is null");
        }

    }

    // TODO
    @Test
    void testIfCorrectEdgeDatacentersCreated() {
        List<ConfigurationEntity.EdgeDataCenterEntity> entities = new ArrayList<>();
        ConfigurationEntity.EdgeDataCenterEntity edgeDataCenterEntity;
        for (int i = 0; i < 50; i++) {
            if (i % 2 == 0) {
                edgeDataCenterEntity = new ConfigurationEntity.EdgeDataCenterEntity();
            } else {
                edgeDataCenterEntity = new ConfigurationEntity.EdgeBlockchainDeviceEntity();
                ConfigurationEntity.EdgeBlockchainDeviceEntity blockchainDeviceEntity = (ConfigurationEntity.EdgeBlockchainDeviceEntity) edgeDataCenterEntity;
            }

        }
    }

    private static ConfigurationEntity buildTopologyFromFile(String filePath) {
        System.out.println("Creating topology from file " + filePath);
        ConfigurationEntity conf  = null;
        try (FileReader jsonFileReader = new FileReader(filePath)){
            conf = topologyBuilder.parseTopology(jsonFileReader);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: input configuration file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Topology built:");
        return conf;
    }

    @Test
    void testCreateIoTDevices() {
        List<ConfigurationEntity.IotDeviceEntity> list = new ArrayList<>();
        ConfigurationEntity.IotDeviceEntity entity;
        for (int i = 0; i < 100; i++) {
            entity = new ConfigurationEntity.IotDeviceEntity();
            entity.setMobilityEntity(new ConfigurationEntity.MobilityEntity());
            entity.setName("dupa "+i);
            if (i % 2 == 0) {
                entity.setIoTClassName("org.cloudbus.cloudsim.edge.iot.TemperatureSensor");
            } else {
                entity.setIoTClassName("org.cloudbus.blockchain.devices.sensors.TemperatureSensor");
            }
        }

    }

}
