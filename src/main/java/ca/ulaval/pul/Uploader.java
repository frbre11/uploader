package ca.ulaval.pul;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Uploader {
	//private static final String PUL_SERVICES_URL = "https://pul-services.dev.pulsar.ca/pul/v1/individus/participants/documents/idparticipant/810?typedocument=PERSPECLettreParticipante";
	private static final String PUL_SERVICES_URL = "http://localhost:8080/pul/v1/individus/participants/documents/idparticipant/810?typedocument=PERSPECLettreParticipante";
	
    Uploader(){
    }
    
    public void upload(String filename, String idDocument) throws FileNotFoundException{
    	File sourceFile = new File(filename);
    	
        if(!sourceFile.exists()) {
        	throw new FileNotFoundException();     
        }
        String accessToken = Utils.getAccessToken(Utils.KEYCLOAK_TOKEN_URL);
        try (
        	CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        )
        {
            HttpEntity data = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("fichier", sourceFile, ContentType.DEFAULT_BINARY, sourceFile.getName())
                    .addTextBody("titre", "Titre de mon document", ContentType.DEFAULT_TEXT)
                    .build();

            HttpUriRequest request = null;
            if (idDocument == null) {
	            request = RequestBuilder
	                    .post(PUL_SERVICES_URL)
	                    .setEntity(data)
	                    .setHeader("Authorization", "Bearer " + accessToken)
	                    .build();
            } else {
	            request = RequestBuilder
	                    .put(PUL_SERVICES_URL + "&iddocument=" + idDocument)
	                    .setEntity(data)
	                    .setHeader("Authorization", "Bearer " + accessToken)
	                    .build();
            }

            System.out.println("Executing request " + request.getRequestLine());

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(request, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
    	if (args.length == 0) {
    		System.err.println("Usage: Uploader <filename> [<iddocument>]");
    		System.exit(-1);
    	}
    	String filename = args[0];
    	String iddocument = null;
    	if (args.length == 2) {
    		iddocument = args[1];
    	}
        Uploader uploader = new Uploader();
        try {
			uploader.upload(filename, iddocument);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
    }

}
