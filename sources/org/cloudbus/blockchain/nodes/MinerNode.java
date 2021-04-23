package org.cloudbus.blockchain.nodes;

public class MinerNode extends BaseNode {

    // Total number of mined blocks
    private int noOfMinedBlocks;

    public MinerNode() {
        super();
        noOfMinedBlocks = 0;
    }

    public int getNoOfMinedBlocks() {
        return noOfMinedBlocks;
    }


}
