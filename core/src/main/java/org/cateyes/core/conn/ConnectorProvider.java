package org.cateyes.core.conn;

public class ConnectorProvider {
	
	
	public static HttpConnector getCommonConnector(){
		return ApacheConnector.getInstance();
	}
	
}
