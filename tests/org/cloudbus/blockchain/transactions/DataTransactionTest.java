package org.cloudbus.blockchain.transactions;

import org.cloudbus.blockchain.nodes.Node;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.edge.core.edge.EdgeLet;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class DataTransactionTest {
    private final static Random random = new Random();
    private static final DataTransaction sampleTransaction = new DataTransaction(new Node(), new Node(), new EdgeLet(random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt(), new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull()));

    public static DataTransaction getSampleDataTransaction(){
        return sampleTransaction;
    }

}
