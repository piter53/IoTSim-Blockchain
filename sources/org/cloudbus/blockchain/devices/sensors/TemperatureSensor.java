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

package org.cloudbus.blockchain.devices.sensors;

import org.cloudbus.blockchain.devices.IoTBlockchainDevice;
import org.cloudbus.blockchain.nodes.BaseNode;
import org.cloudbus.blockchain.policies.TransmissionPolicy;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.edge.iot.IoTDevice;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetworkInfo;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class TemperatureSensor extends IoTBlockchainDevice {


	public TemperatureSensor(EdgeNetworkInfo networkModel, String name, double bandwidth, BaseNode node, TransmissionPolicy transmissionPolicy) {
		super(name, networkModel, bandwidth, node, transmissionPolicy);
	}

	@Override
	public boolean updateBatteryBySensing() {
		battery.setCurrentCapacity(battery.getCurrentCapacity() - battery.getBatterySensingRate());
		if(battery.getCurrentCapacity()<0)
			return  true;
		return false;
	}

	@Override
	public boolean updateBatteryByTransmission() {
		battery.setCurrentCapacity(battery.getCurrentCapacity() - battery.getBatterySendingRate());
		if(battery.getCurrentCapacity()<0)
			return  true;
		return false;
	}

	@Override
	public void startEntity() {
		super.startEntity();
	}

	@Override
	public void processEvent(SimEvent ev) {
		super.processEvent(ev);
	}

	@Override
	public void shutdownEntity() {
	}
}
