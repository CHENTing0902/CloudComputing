package org.ctlv.proxmox.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(Map<String, List<LXC>> myCTsPerServer)  {
		HashMap<String, Float> memCTsPerServer = new HashMap<>();
		
		
		// Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
		Set<String> keySet = myCTsPerServer.keySet();
		for (String key : keySet) {
			List<LXC> cts = myCTsPerServer.get(key);
			for (LXC ctn : cts) {
				if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
					memCTsPerServer.put(key, memCTsPerServer.get(key) + ctn.getMem());
				}
			}
		}
		
		
		// M�moire autoris�e sur chaque serveur
		// ...

		
		// Analyse et Actions
		// ...
		
	}

}
