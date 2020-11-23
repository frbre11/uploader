package ca.ulaval.pul;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	public static final String KEYCLOAK_TOKEN_URL = "https://authk.dev.ulaval.ca/auth/realms/pulsar/protocol/openid-connect/token";

    public static String getAccessToken(String keycloakTokenUrl) {
    	String accessToken = null;
        HttpPost post = new HttpPost(keycloakTokenUrl);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        ArrayList<NameValuePair> parameters;
        parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("grant_type", "password"));
        parameters.add(new BasicNameValuePair("client_id", "ul-pul-dv-ui"));
        parameters.add(new BasicNameValuePair("username", "francoisditlebreton@gmail.com"));
        parameters.add(new BasicNameValuePair("password", "Password123"));
        try {
			post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String result = EntityUtils.toString(response.getEntity());
            //System.out.println("result: " + result.toString());
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
			Map<String, String> map = mapper.readValue(result, Map.class);
            accessToken = map.get("access_token");
            System.out.println("accessToken: " + accessToken);
        } catch (IOException e) {
			e.printStackTrace();
		}
		return accessToken;
	}
}
