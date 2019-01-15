package org.ctlv.proxmox.tester;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.data.Node;
import org.json.JSONException;


public class Main {

	public static void main(String[] args) throws LoginException, JSONException, IOException {

		ProxmoxAPI api = new ProxmoxAPI();		
		
		// Listes les CTs par serveur
//		for (int i=1; i<=10; i++) {
//			String srv ="srv-px"+i;
//			System.out.println("CTs sous "+srv);
//			List<LXC> cts = api.getCTs(srv);
//			
//			for (LXC lxc : cts) {
//				System.out.println("\t" + lxc.getName());
//			}
//		}
		
		DecimalFormat df = new DecimalFormat("0.##");
		
		Node node = api.getNode("srv-px5");
		System.out.println("Server CPU usage : " + df.format(node.getCpu() * 100) + "%");
		
		System.out.println("Server Disk usage : " + df.format(node.getRootfs_total() /(Math.pow(2, 30))) + "%");
		
		System.out.println("Server Memory usage : " + df.format((float) node.getMemory_used() / (float) node.getMemory_total()) + "%");
		
		// Cr�er un CT
//		api.createCT("srv-px5", "2100", "ct-tpgei-virt-B1-ct5", 512);
		
		LXC ctn = api.getCT("srv-px5", "2000");
		
		System.out.println("Container CPU usage : " + df.format(ctn.getCpu() * 100) + "%");
		System.out.println("Container Disk usage : " + df.format(ctn.getDisk() / (Math.pow(2, 30))) + "%");
		System.out.println("Container Memory usage : " + df.format((float) ctn.getMem() / (float) ctn.getMaxmem()) + "%");
		System.out.println("Host server name : " );
		
//		api.startCT("srv-px5", "2000");
		
		
		
		// Supprimer un CT
//		api.deleteCT("srv-px5", "2100");

		
	}

}
