package org.cateyes.core;

public class ConnectorProvider {
	
	
	public static ApacheConnector getCommonConnector(){
		return ApacheConnector.getInstance();
	}
	
}
