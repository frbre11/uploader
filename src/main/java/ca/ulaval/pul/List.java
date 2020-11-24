package ca.ulaval.pul;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class List {
	//private static final String PUL_SERVICES_URL = "https://pul-services.dev.pulsar.ca/pul/v1/individus/participants/documents/idparticipant/810?typedocument=PERSPECLettreParticipante";
	private static final String PUL_SERVICES_URL = "http://localhost:8080/pul/v1/individus/participants/documents/idparticipant/810";
	
    List(){
    }
    
    public void list() throws FileNotFoundException{
    	HttpGet get = null;
    	
        String accessToken = Utils.getAccessToken(Utils.KEYCLOAK_TOKEN_URL);
        try (
        	CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        )
        {          
            get = new HttpGet(PUL_SERVICES_URL);
            get.addHeader("Authorization", "Bearer " + accessToken);
            InputStream inputStream = httpclient.execute(get).getEntity().getContent();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
            	System.out.println(line);
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
        List list = new List();
        try {
        	list.list();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
    }

}
