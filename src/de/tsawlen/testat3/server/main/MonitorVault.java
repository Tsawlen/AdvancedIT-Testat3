package de.tsawlen.testat3.server.main;

import java.util.HashMap;
import java.util.Map;

public class MonitorVault {
	
	Map<String, FileMonitor> monitor = new HashMap<>();
	
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
