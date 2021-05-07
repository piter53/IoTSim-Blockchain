package org.cloudbus.blockchain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Piotr Grela
 */
@Data
public class Consensus {

    @Getter @Setter
    private static double blockGenerationReward = 1;

    @Getter
    private static double transactionFee = 0.1;

    @Getter
    private static Network network = Network.getInstance();

}
