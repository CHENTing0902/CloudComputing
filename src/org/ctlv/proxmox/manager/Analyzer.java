package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(Map<String, List<LXC>> myCTsPerServer) throws LoginException, JSONException, IOException, InterruptedException  {
		HashMap<String, Float> memCTsPerServer = new HashMap<>();
		
		
		// Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
		Set<String> keySet = myCTsPerServer.keySet();
		for (String key : keySet) {
			if (!memCTsPerServer.containsKey(key)) {
				memCTsPerServer.put(key,0f);
			}
			List<LXC> cts = myCTsPerServer.get(key);
			for (LXC ctn : cts) {
				if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
					memCTsPerServer.put(key, memCTsPerServer.get(key) + ctn.getMem());
				}
			}
		}
		
		
		// M�moire autoris�e sur chaque serveur
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		
		// Analyse et Actions
		
		Float memOnServer1 = memCTsPerServer.get(Constants.SERVER1);
		Float memOnServer2 = memCTsPerServer.get(Constants.SERVER2);
		
		
		while (memOnServer1!= null && memOnServer1 > (memAllowedOnServer1 / 10) && memOnServer1 < (memAllowedOnServer1 * 3 / 4)) {
			controller.migrateFromTo(Constants.SERVER1, Constants.SERVER2);
			System.out.println("Migrate from " + Constants.SERVER1 + " to " + Constants.SERVER2);
		}
		
		while (memOnServer2!= null && memOnServer2 > (memAllowedOnServer2 / 2) && memOnServer2 < (memAllowedOnServer2 * 3 / 4)) {
			controller.migrateFromTo(Constants.SERVER2, Constants.SERVER1);
			System.out.println("Migrate from " + Constants.SERVER2 + " to " + Constants.SERVER1);
		}
		
		while (memOnServer1!= null && memOnServer1 > (memAllowedOnServer1 * 3 / 4)) {
			controller.offLoad(Constants.SERVER1);
			System.out.println("Stop from " + Constants.SERVER1);
		}
		
		while (memOnServer2!= null && memOnServer2 > (memAllowedOnServer2 * 3 / 4)) {
			controller.offLoad(Constants.SERVER2);
			System.out.println("Stop from " + Constants.SERVER2);
		}
		
	}

}
