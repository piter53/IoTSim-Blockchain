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

package org.cloudbus.blockchain.examples;

import org.cloudbus.blockchain.BlockchainBroker;
import org.cloudbus.blockchain.BlockchainBuilder;
import org.cloudbus.blockchain.Network;
import org.cloudbus.blockchain.consensus.ConsensusProtocol;
import org.cloudbus.blockchain.consensus.ProofOfWork;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicy;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicySizeBased;
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.examples.util.PrintBlockchainResults;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.edge.core.edge.ConfigurationEntity;
import org.cloudbus.cloudsim.edge.core.edge.MEL;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.osmesis.examples.uti.LogPrinter;
import org.cloudbus.cloudsim.osmesis.examples.uti.PrintResults;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.osmosis.core.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * @author Khaled Alwasel, modified by Piotr Grela
 * @since IoTSim-Blockchain 1.0
 * 
**/

public class BlockchainExample_1 {
	public static final String configurationFile = "inputFiles/BlockchainExample1_configuration_1.json";
	public static final String osmesisAppFile =  "inputFiles/Example1_Workload.csv";
    OsmosisBuilder topologyBuilder;
	OsmesisBroker blockchainBroker;
	List<OsmesisDatacenter> datacenters;
	List<MEL> melList;	
	EdgeSDNController edgeSDNController;
	List<Vm> vmList;

	public static void main(String[] args) throws Exception {
		BlockchainExample_1 simulation = new BlockchainExample_1();
		simulation.start();
	}
	
	public void start() throws Exception{

	    // number of users
		int num_user = 1;
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		// Initialize the CloudSim library
		CloudSim.init(num_user, calendar, trace_flag);
		blockchainBroker = new BlockchainBroker("blockchainBroker");
		topologyBuilder = new BlockchainBuilder(blockchainBroker);
		ConfigurationEntity config = buildTopologyFromFile(configurationFile);
        if(config !=  null) {
        	topologyBuilder.buildTopology(config);
        }
        
        OsmosisOrchestrator maestro = new OsmosisOrchestrator();
        
		OsmesisAppsParser.startParsingExcelAppFile(osmesisAppFile);
		List<SDNController> controllers = new ArrayList<>();
		for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
			blockchainBroker.submitVmList(osmesisDC.getVmList(), osmesisDC.getId());
			controllers.add(osmesisDC.getSdnController());
			osmesisDC.getSdnController().setWanOorchestrator(maestro);			
		}
		controllers.add(topologyBuilder.getSdWanController());
		maestro.setSdnControllers(controllers);
		blockchainBroker.submitOsmesisApps(OsmesisAppsParser.appList);
		blockchainBroker.setDatacenters(topologyBuilder.getOsmesisDatacentres());

		// Add 5 units of currency to each IoTBlockchainDevice so that they can afford to broadcast transactions with sensed data
        for (IoTBlockchainDevice device : Network.getInstance().getIoTBlockchainDevicesSet()) {
            device.getBlockchainNode().addBalance(5);
        }

//        TransmissionPolicy transmissionPolicy = new TransmissionPolicySizeBased((long)100);
//        ConsensusProtocol consensusProtocol = new ProofOfWork(transmissionPolicy,10,1,1,0.01,0.01, 300);
//        Network.setConsensus(consensusProtocol);

		double startTime = CloudSim.startSimulation();
  
		LogUtil.simulationFinished();
		PrintBlockchainResults printer = new PrintBlockchainResults();
		printer.printAverageBlockchainStats();
		printer.printTransactionDestinationShares();
		Log.printLine();
		Log.printLine("Simulation Finished!");

	}
	
    private ConfigurationEntity buildTopologyFromFile(String filePath) throws Exception {
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
	
	public void setEdgeSDNController(EdgeSDNController edc) {
		this.edgeSDNController = edc;
	}
}
