package org.cloudbus.osmosis.core;

import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class OsmosisBuilderTest {

    static final String configurationFile = "inputFiles/Example1_configuration.json";
    static final String osmesisAppFile =  "inputFiles/Example1_Worload.csv";
    static OsmosisBuilder topologyBuilder;
    static OsmesisBroker osmesisBroker;

    @BeforeAll
    static void setUp () {
        osmesisBroker = new OsmesisBroker("OsmesisBroker");
        topologyBuilder = new OsmosisBuilder(osmesisBroker);
        ConfiguationEntity config = buildTopologyFromFile(configurationFile);
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

    private static ConfiguationEntity buildTopologyFromFile(String filePath) {
        System.out.println("Creating topology from file " + filePath);
        ConfiguationEntity conf  = null;
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

}
