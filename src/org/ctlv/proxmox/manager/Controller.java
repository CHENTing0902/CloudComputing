package org.ctlv.proxmox.manager;

import org.ctlv.proxmox.api.ProxmoxAPI;

public class Controller {

	ProxmoxAPI api;
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer)  {
//		api.migrateCT(srcServer, ctID, dstServer);
	}

	// arr�ter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) {
//		api.stopCT(server, ctID);
	}

}
