package ca.ulaval.pul;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Downloader {
	//private static final String PUL_SERVICES_URL = "https://pul-services.dev.pulsar.ca/pul/v1/individus/participants/documents/idparticipant/810?typedocument=PERSPECLettreParticipante";
	private static final String PUL_SERVICES_URL = "http://localhost:8080/pul/v1/individus/participants/documents/content/idparticipant/810?iddocument=";
	
    Downloader(){
    }
    
    public void download(String idDocument, String filename) throws FileNotFoundException{
    	HttpGet get = null;
    	
        String accessToken = Utils.getAccessToken(Utils.KEYCLOAK_TOKEN_URL);
        try (
        	CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        	FileOutputStream outputStream = new FileOutputStream(new File(filename));
        )
        {          
            get = new HttpGet(PUL_SERVICES_URL + idDocument);
            get.addHeader("Authorization", "Bearer " + accessToken);
            InputStream inputStream = httpclient.execute(get).getEntity().getContent();
            
            byte[] buffer = new byte[ 8192 ];
            int numRead;
            while ((numRead = inputStream.read(buffer)) > 0) {
            	outputStream.write(buffer, 0, numRead);
            }
        } catch (IOException e) {
				e.printStackTrace();
		} finally {
			if (get != null) {
				get.completed();
			}
		}
    }

    public static void main(String[] args) {
    	if (args.length < 2) {
    		System.err.println("Usage: Downloader <iddocument> <filename>");
    		System.exit(-1);
    	}
    	String idDocument = args[0];
    	String filename = args[1];
        Downloader downloader = new Downloader();
        try {
        	downloader.download(idDocument, filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
    }

}
