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

import org.cloudbus.blockchain.examples.util.BlockchainPrintResults;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.edge.core.edge.ConfigurationEntity;
import org.cloudbus.cloudsim.edge.core.edge.MEL;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.osmesis.examples.uti.LogPrinter;
import org.cloudbus.osmosis.core.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class DissertationEvaluation1osmosis {
	public static final String configurationFile = "inputFiles/dissertation/evaluation/OsmosisConfiguration-Evaluation.json";
	public static final String osmesisAppFile =  "inputFiles/dissertation/evaluation/Workload_Evaluation1.csv";
    OsmosisBuilder topologyBuilder;
	OsmesisBroker osmesisBroker;
	List<OsmesisDatacenter> datacenters;
	List<MEL> melList;	
	EdgeSDNController edgeSDNController;
	List<Vm> vmList;

	public static void main(String[] args) throws Exception {
		DissertationEvaluation1osmosis osmesis = new DissertationEvaluation1osmosis();
		osmesis.start();
	}
	
	public void start() throws Exception{

		int num_user = 1; // number of users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		// Initialize the CloudSim library
		CloudSim.init(num_user, calendar, trace_flag);
		osmesisBroker  = new OsmesisBroker("OsmesisBroker");
		topologyBuilder = new OsmosisBuilder(osmesisBroker);
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
		
		double startTime = CloudSim.startSimulation();
  
		LogUtil.simulationFinished();
//		PrintResults pr = new PrintResults();
//		pr.printOsmesisNetwork();
        BlockchainPrintResults blockchainPrintResults = new BlockchainPrintResults();
        blockchainPrintResults.readAverageOsmoticAppTimes();
//        blockchainPrintResults.printOsmesisApps();
        blockchainPrintResults.writeOsmesisAppsToFile("osmosisEval.txt");

			
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
