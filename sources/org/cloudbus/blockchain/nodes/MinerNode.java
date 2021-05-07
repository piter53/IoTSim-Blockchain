package org.cloudbus.blockchain.nodes;

public class MinerNode extends BaseNode {

    // Total number of mined blocks
    private int noOfMinedBlocks;

    private int hashpower;

    public MinerNode() {
        this(0, 0);
    }

    public MinerNode(Integer blockchainDepth, int hashpower) {
        super(blockchainDepth);
        noOfMinedBlocks = 0;
    }

    public int getNoOfMinedBlocks() {
        return noOfMinedBlocks;
    }

    public int getHashpower() {
        return hashpower;
    }

    public void setHashpower(int hashpower) {
        this.hashpower = hashpower;
    }

}
