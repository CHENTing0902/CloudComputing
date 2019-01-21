package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.data.Node;
import org.json.JSONException;



public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
		
	
		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		
		int last_vmid = (int) Constants.CT_BASE_ID;
		
		while (true) {
			
			// 1. Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
			long memOnServer1 = 0;
			Node server1 = api.getNode(Constants.SERVER1);
			List<LXC> cts1 = api.getCTs(Constants.SERVER1);
			for (LXC ctn : cts1) {
				if ((baseID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
					memOnServer1 += ctn.getMem();
					last_vmid = Math.max(last_vmid, Integer.parseInt(ctn.getVmid()));
				}
			}
			System.out.println("mem cts1 : " + memOnServer1);
			System.out.println("mem srv5 : " + server1.getMemory_total());
			float memOnCTS1 = (float) memOnServer1 / (float) server1.getMemory_total();
			System.out.println(memOnCTS1*100 + "%");
			
			long memOnServer2 = 0;
			Node server2 = api.getNode(Constants.SERVER2);
			List<LXC> cts2 = api.getCTs(Constants.SERVER2);
			for (LXC ctn : cts2) {
				if ((Constants.CT_BASE_ID / 100) == (Integer.parseInt(ctn.getVmid()) / 100)) {
					memOnServer2 += ctn.getMem();
					last_vmid = Math.max(last_vmid, Integer.parseInt(ctn.getVmid()));
				}
			}
			System.out.println("mem cts2 : " + memOnServer2);
			System.out.println("mem srv6 : " + server2.getMemory_total());
			float memOnCTS2 = (float) memOnServer2 / (float) server2.getMemory_total();
			System.out.println(memOnCTS2*100 + "%");
			
			// M�moire autoris�e sur chaque serveur
			float memRatioOnServer1 = 0;
			memRatioOnServer1 = memAllowedOnServer1 - 512;
			float memRatioOnServer2 = Constants.MAX_THRESHOLD;
			memRatioOnServer2 = memAllowedOnServer2 - 512;
			
			if (memOnServer1 < memRatioOnServer1 && memOnServer2 < memRatioOnServer2) {  // Exemple de condition de l'arr�t de la g�n�ration de CTs
				
				// choisir un serveur al�atoirement avec les ratios sp�cifi�s 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1)
					serverName = Constants.SERVER1;
				else
					serverName = Constants.SERVER2;
				
				// cr�er un contenaire sur ce serveur
				int new_vmid = last_vmid + 1;
				System.out.println("Creating CT " + new_vmid + " ....");
				api.createCT(serverName, Integer.toString(new_vmid), Constants.CT_BASE_NAME + Integer.toString(new_vmid%100), 512);
//				Thread.sleep(Constants.GENERATION_WAIT_TIME * 1000);
//				api.startCT(serverName, Integer.toString(new_vmid));
				
				// planifier la prochaine cr�ation
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain �v�nement
				Thread.sleep(1000 * timeToWait);
			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME * 1000);
			}
		}
		
	}

}
