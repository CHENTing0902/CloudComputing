package org.ctlv.proxmox.manager;

import java.util.HashMap;
import java.util.List;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;import com.sun.corba.se.impl.orbutil.closure.Constant;

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
//			myCTsPerServer.put(Constants.SERVER1, api.getCTs(Constants.SERVER1));
//			myCTsPerServer.put(Constants.SERVER2, api.getCTs(Constants.SERVER2));
			analyzer.analyze(myCTsPerServer);

			
			// attendre une certaine p�riode
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
