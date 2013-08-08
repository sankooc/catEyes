package org.cateyes.core.conn;

public class ConnectorProvider {
	
	
	public static ApacheConnector getCommonConnector(){
		return ApacheConnector.getInstance();
	}
	
}
