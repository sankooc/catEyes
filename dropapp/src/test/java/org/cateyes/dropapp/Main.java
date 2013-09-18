package org.cateyes.dropapp;

import java.io.OutputStream;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		final String APP_KEY = DropAppsContext.key;
        final String APP_SECRET = DropAppsContext.value;

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig(
            "JavaTutorial/1.0", Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = null;
        
        DbxAuthFinish authFinish = webAuth.finish(code);
        
        DbxClient client = new DbxClient(config, authFinish.accessToken);
        System.out.println("Linked account: " + client.getAccountInfo().displayName);
        DbxEntry entry =  client.getMetadata("/cateyes");
        if(null == entry){
        	entry = client.createFolder("/cateyes");
        }else if(entry.isFile()){
        	return;//TODO
        }
        
        
//        OutputStream outputStream = null;
//        DbxEntry.File downloadedFile = client.getFile("/magnum-opus.txt", null,
//                outputStream);
//        System.out.println("Files in the root path:");
//        for (DbxEntry child : listing.children) {
//            System.out.println("	" + child.name + ": " + child.toString());
//        }
        
	}

}
