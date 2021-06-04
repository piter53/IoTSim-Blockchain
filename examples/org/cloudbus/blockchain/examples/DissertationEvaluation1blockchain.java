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
import org.cloudbus.blockchain.consensus.ProofOfWork;
import org.cloudbus.blockchain.consensus.policies.TransmissionPolicySizeBased;
import org.cloudbus.blockchain.devices.BlockchainDevice;
import org.cloudbus.blockchain.examples.util.PrintBlockchainResults;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.edge.core.edge.ConfigurationEntity;
import org.cloudbus.cloudsim.edge.core.edge.MEL;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
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

public class DissertationEvaluation1blockchain {
	public static final String configurationFile = "inputFiles/dissertation/evaluation/Configuration-Evaluation1.json";
	public static final String osmesisAppFile =  "inputFiles/dissertation/evaluation/Workload_Evaluation1.csv";
    OsmosisBuilder topologyBuilder;
	OsmesisBroker broker;
	List<OsmesisDatacenter> datacenters;
	List<MEL> melList;	
	EdgeSDNController edgeSDNController;
	List<Vm> vmList;

	public static void main(String[] args) throws Exception {
	    ProofOfWork proofOfWork = new ProofOfWork(new TransmissionPolicySizeBased((long)100), 3, 1, 1, 0.001, 0.01, 300);
        DissertationEvaluation1blockchain osmesis = new DissertationEvaluation1blockchain();
        Network.setConsensus(proofOfWork);
        osmesis.start();
        PrintBlockchainResults printBlockchainResults = new PrintBlockchainResults();
        printBlockchainResults.printAverageBlockchainStats();
        printBlockchainResults.printTransactionDestinationShares();
    }

	public void start() throws Exception{

		int num_user = 1; // number of users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events
		// Initialize the CloudSim library
		CloudSim.init(num_user, calendar, trace_flag);
		broker = new BlockchainBroker("OsmesisBroker");
		topologyBuilder = new BlockchainBuilder(broker);
		ConfigurationEntity config = buildTopologyFromFile(configurationFile);
        if(config !=  null) {
        	topologyBuilder.buildTopology(config);
        }

        OsmosisOrchestrator maestro = new OsmosisOrchestrator();
        
		OsmesisAppsParser.startParsingExcelAppFile(osmesisAppFile);
		List<SDNController> controllers = new ArrayList<>();
		for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
			broker.submitVmList(osmesisDC.getVmList(), osmesisDC.getId());
			controllers.add(osmesisDC.getSdnController());
			osmesisDC.getSdnController().setWanOorchestrator(maestro);			
		}
		controllers.add(topologyBuilder.getSdWanController());
		maestro.setSdnControllers(controllers);
		broker.submitOsmesisApps(OsmesisAppsParser.appList);
		broker.setDatacenters(topologyBuilder.getOsmesisDatacentres());

		// Set each BlockchainDevice to possess some initial amount of currency so that they can afford to broadcast transactions.
        for (BlockchainDevice device : Network.getInstance().getBlockchainDevicesSet()) {
            device.getBlockchainNode().addBalance(Integer.MAX_VALUE/2.0);
        }

        // Perform the simulation and print results
		double startTime = CloudSim.startSimulation();
  
		LogUtil.simulationFinished();
//		PrintBlockchainResults printBlockchainResults = new PrintBlockchainResults();
//		printBlockchainResults.readAverageOsmoticAppTimes();
//		blockchainPrintResults.printOsmesisApps();
//        printBlockchainResults.writeOsmesisAppsToFile("blockchainEval.txt");
//        printBlockchainResults.printAverageBlockchainStats();
//        printBlockchainResults.printTransactionDestinationShares();
//        printBlockchainResults.writeAverageBlockchainStats("avgBlockchainStats.csv", false, 1);
//		PrintResults printResults = new PrintResults();
//		printResults.printOsmesisNetwork();
			
		Log.printLine();

//		for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
//			List<Switch> switchList = osmesisDC.getSdnController().getSwitchList();
//			LogPrinter.printEnergyConsumption(osmesisDC.getName(), osmesisDC.getSdnhosts(), switchList, startTime);
//			Log.printLine();
//		}
		
//		Log.printLine();
//		LogPrinter.printEnergyConsumption(topologyBuilder.getSdWanController().getName(), null, topologyBuilder.getSdWanController().getSwitchList(), startTime);
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
