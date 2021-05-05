package org.cloudbus.blockchain.nodes;

public class MinerNode extends BaseNode {

    // Total number of mined blocks
    private int noOfMinedBlocks;

    public MinerNode() {
        this(0);
    }

    public MinerNode(Integer blockchainDepth) {
        super(blockchainDepth);
        noOfMinedBlocks = 0;
    }

    public int getNoOfMinedBlocks() {
        return noOfMinedBlocks;
    }


}
