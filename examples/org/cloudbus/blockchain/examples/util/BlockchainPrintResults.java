package org.cloudbus.blockchain.examples.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;
import org.cloudbus.osmosis.core.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Grela
 */
public class BlockchainPrintResults extends org.cloudbus.cloudsim.osmesis.examples.uti.PrintResults {

    private List<OsmesisAvgAppData> tagsPerApp = new ArrayList<>();

    public void readAverageOsmoticAppTimes() {
        tagsPerApp.clear();
        for(OsmesisAppDescription app : OsmesisAppsParser.appList){
            int i = 0;
            OsmesisAvgAppData avgTag = new OsmesisAvgAppData();
            for(WorkflowInfo workflowTag : OsmesisBroker.workflowTag){
                workflowTag.getAppId();
                if(app.getAppID() == workflowTag.getAppId()){
                    avgTag.setAppId(workflowTag.getAppId());
                    avgTag.IoTtoMelTransmissionTime += workflowTag.getIotDeviceFlow().getTransmissionTime();
                    avgTag.MELStart += workflowTag.getEdgeLet().getExecStartTime() - workflowTag.getSartTime();
                    double transactionTotalTime =  workflowTag.getIotDeviceFlow().getTransmissionTime() + workflowTag.getEdgeLet().getActualCPUTime()
                        + workflowTag.getEdgeToCloudFlow().getTransmissionTime() + workflowTag.getCloudLet().getActualCPUTime();
                    avgTag.totalTime += transactionTotalTime;
                    i++;
                }
            }
            avgTag.totalTime = avgTag.totalTime / i;
            avgTag.MELStart = avgTag.MELStart / i;
            avgTag.IoTtoMelTransmissionTime = avgTag.IoTtoMelTransmissionTime / i;
            tagsPerApp.add(avgTag);
        }
    }



    @Data
    public class OsmesisAvgAppData {

        int appId = 0;
        @Setter @Getter
        double IoTtoMelTransmissionTime = 0.0;
        @Setter @Getter
        double MELStart = 0.0;
        @Setter @Getter
        double totalTime = 0.0;

        @Override
        public String toString(){
            DecimalFormat format = new DecimalFormat("0.00");
            return appId + "," + format.format(IoTtoMelTransmissionTime) + "," + format.format(MELStart) + "," + format.format(totalTime) + "\n";
        }
//        @Getter
//        long noOfEntries = 0;

//        public void appendIoTtoMelTransmissionTime(double time) {
//            IoTtoMelTransmissionTime += time;
//        }

//        public void appendMELStart(double time) {
//            MELStart += time;
//        }

//        public void appendTotalTime(double time) {
//            totalTime += time;
//        }

//        public void averageAll(){
//
//        }
    }

    public String osmoticAppsStatsHeader = "\"App ID\"" + "," + "\"Average transmission time from IoT device to MEL\"" + "," + "\"Average MEL start time\"" + "," + "\"Average total run time\"" + "\n";
        ;

    public void printOsmesisApps() {
        Log.printLine();
        Log.printLine("=========================== Osmesis App Results ========================");
        Log.printLine(String.format("%1s %37s %29s %25s %10s"
            ,"App_ID"
            ,"AvgTransmissionTimeIoTDeviceToMEL"
            ,"AvgEdgeLet_MEL_StartTime"
            , "AvgTransactionTotalTime"
            , "   "));

        for(OsmesisAvgAppData appTag : tagsPerApp){ ;
            Log.printLine(String.format("%1s %34s %24s %28s"
                , appTag.appId
                , new DecimalFormat("0.00").format(appTag.IoTtoMelTransmissionTime)
                , new DecimalFormat("0.00").format(appTag.MELStart)
                , new DecimalFormat("0.00").format(appTag.totalTime)));
        }
    }

    public void writeOsmesisAppsToFile(String filename){
        try {
            FileWriter fileWriter = new FileWriter("outputFiles/evaluation/" + filename, false);
            fileWriter.write(osmoticAppsStatsHeader);
            for (OsmesisAvgAppData appData : tagsPerApp) {
                fileWriter.write(appData.toString());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
