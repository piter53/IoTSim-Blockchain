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
import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
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

public class BlockchainExample_1_Dissertation {
	public static final String configurationFile = "inputFiles/BlockchainExample1_configuration_Dissertation.json";
	public static final String osmesisAppFile =  "inputFiles/Example_Workload_Dissertation.csv";
    OsmosisBuilder topologyBuilder;
	OsmesisBroker osmesisBroker;
	List<OsmesisDatacenter> datacenters;
	List<MEL> melList;	
	EdgeSDNController edgeSDNController;
	List<Vm> vmList;

	public static void main(String[] args) throws Exception {
		BlockchainExample_1_Dissertation osmesis = new BlockchainExample_1_Dissertation();
		osmesis.start();
	}
	
	public void start() throws Exception{

		int num_user = 1; // number of users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		// Initialize the CloudSim library
		CloudSim.init(num_user, calendar, trace_flag);
		osmesisBroker  = new BlockchainBroker("OsmesisBroker");
		topologyBuilder = new BlockchainBuilder(osmesisBroker);
		ConfigurationEntity config = buildTopologyFromFile(configurationFile);
        if(config !=  null) {
        	topologyBuilder.buildTopology(config);
        }
        
        OsmosisOrchestrator maestro = new OsmosisOrchestrator();
        
		OsmesisAppsParser.startParsingExcelAppFile(osmesisAppFile);
		List<SDNController> controllers = new ArrayList<>();
		for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
			osmesisBroker.submitVmList(osmesisDC.getVmList(), osmesisDC.getId());
			controllers.add(osmesisDC.getSdnController());
			osmesisDC.getSdnController().setWanOorchestrator(maestro);			
		}
		controllers.add(topologyBuilder.getSdWanController());
		maestro.setSdnControllers(controllers);
		osmesisBroker.submitOsmesisApps(OsmesisAppsParser.appList);
		osmesisBroker.setDatacenters(topologyBuilder.getOsmesisDatacentres());

		// Add 5 units of currency to each IoTBlockchainDevice so that they can afford to broadcast transactions with sensed data
        for (IoTBlockchainDevice device : Network.getInstance().getIoTBlockchainDevicesSet()) {
            device.getBlockchainNode().addBalance(5);
        }

		double startTime = CloudSim.startSimulation();
  
		LogUtil.simulationFinished();
		PrintResults pr = new PrintResults();
		pr.printOsmesisNetwork();
			
		Log.printLine();

		for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){		
			List<Switch> switchList = osmesisDC.getSdnController().getSwitchList();
			LogPrinter.printEnergyConsumption(osmesisDC.getName(), osmesisDC.getSdnhosts(), switchList, startTime);
			Log.printLine();
		}
		
		Log.printLine();		
		LogPrinter.printEnergyConsumption(topologyBuilder.getSdWanController().getName(), null, topologyBuilder.getSdWanController().getSwitchList(), startTime);
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
