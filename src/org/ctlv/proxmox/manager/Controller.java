package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException, InterruptedException  {
		List<LXC> cts = api.getCTs(srcServer);
		List<LXC> myCts = new ArrayList<>();
		
		for (LXC ctn : cts) {
			if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
				myCts.add(ctn);
			}
		}
		
		String ctID = myCts.get(myCts.size() - 1).getVmid();
		api.stopCT(srcServer, ctID);
		while (api.getCT(srcServer, ctID).getStatus() != "stopped") {
			Thread.sleep(1000);
		}
		api.migrateCT(srcServer, ctID, dstServer);
	}

	// arr�ter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws LoginException, JSONException, IOException {
		List<LXC> cts = api.getCTs(server);
		List<LXC> myCts = new ArrayList<>();
		
		for (LXC ctn : cts) {
			if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
				myCts.add(ctn);
			}
		}
		
		String ctID = myCts.get(0).getVmid();
		api.stopCT(server, ctID);
	}

}
