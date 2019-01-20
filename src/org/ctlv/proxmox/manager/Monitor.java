package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;


public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	

	@Override
	public void run() {
		
		while(true) {
			
			// R�cup�rer les donn�es sur les serveurs
			

			
			// Lancer l'analyse
			
			HashMap<String, List<LXC>> myCTsPerServer = new HashMap<>();

			try {
				List<LXC> cts1 = api.getCTs(Constants.SERVER1);
				List<LXC> cts2 = api.getCTs(Constants.SERVER2);
				
				List<LXC> myCts1 = new ArrayList<>();
				List<LXC> myCts2 = new ArrayList<>();
				
				for (LXC ctn : cts1) {
					if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
						myCts1.add(ctn);
					}
				}
				
				for (LXC ctn : cts2) {
					if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
						myCts2.add(ctn);
					}
				}
				
				myCTsPerServer.put(Constants.SERVER1, myCts1);
				myCTsPerServer.put(Constants.SERVER2, myCts2);
				
				analyzer.analyze(myCTsPerServer);
				
			} catch (LoginException | JSONException | IOException e) {
				e.printStackTrace();
			}

			
			// attendre une certain period
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
