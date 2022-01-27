package de.tsawlen.testat3.server.main;

import java.util.HashMap;
import java.util.Map;

public class MonitorVault {
	
	//The list of monitors
	Map<String, FileMonitor> monitor = new HashMap<>();
	
	/**
	 * This method is responsible for getting the right monitor and create one if not existing already
	 * @param fileName - the identifier for the monitor
	 * @return - the monitor for the file
	 */
	public synchronized FileMonitor getAccess(String fileName) {
		
		if(monitor.keySet().contains(fileName)) {
			return monitor.get(fileName);
		}else {
			FileMonitor newMonitor = new FileMonitor();
			monitor.put(fileName, newMonitor);
			return newMonitor;
		}
	}

}
