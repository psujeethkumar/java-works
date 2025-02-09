package be.luminus.esb.apigw.jwt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.luminus.esb.apigw.exceptions.JwtTokenMalformedException;
import be.luminus.esb.apigw.exceptions.JwtTokenMissingException;

public class JwtTokenVerifier {

	private String publicKeyString;
	private String token;
	private String clientApplicationId;
	private String claim;
	private String tenantId;
	private JsonNode jwksNode;

	public static String json = null;

	public JwtTokenVerifier() {
		this.clientApplicationId = System.getenv("CLIENT_APPLICATION_ID");
		this.tenantId = System.getenv("TENANT_ID");
		this.claim = System.getenv("CLAIM");
		fetchPublicJwks();
	}

	public void validateToken(final String token) throws JwtTokenMalformedException, JwtTokenMissingException, IncorrectClaimException {
		if (token == null || token.trim().length() == 0) {
			throw new JwtTokenMissingException("Token is missing in the request");
		} else {
			this.token = token;
		}
		DecodedJWT jwt = null;
		try {
			// Prepare the PublicKey from JWKS
			getPublicKey();
			jwt = buildJWTVerifier().verify(token);
		} catch (SignatureVerificationException ex) {
			// ex.printStackTrace();
			throw new JwtTokenMalformedException("Invalid JWT signature");
		} catch (CertificateException ex) {
			// ex.printStackTrace();
			throw new JwtTokenMalformedException("Invalid JWT signature");
		} catch (TokenExpiredException ex) {
			// ex.printStackTrace();
			throw new JwtTokenMalformedException("Expired JWT token");
		} catch (JWTVerificationException ex) {
			// ex.printStackTrace();
			throw new JwtTokenMalformedException("Invalid JWT token");
		} catch (IllegalArgumentException ex) {
			// ex.printStackTrace();
			throw new JwtTokenMissingException("JWT claims string is empty");
		}
		Claim aud = jwt.getClaim("aud");
		Claim roles = jwt.getClaim("roles");

		if (aud.isMissing() || aud.asString().length() == 0 || (!aud.asString().equals(clientApplicationId))) {
			throw new MissingClaimException("HTTP 401 Unauthorized : Token issued for invalid audience");
		}
		if (roles.isMissing() || roles.asList(String.class).size() == 0 || (!roles.asList(String.class).contains(claim))) {
			throw new IncorrectClaimException("HTTP 403 Forbidden : Incorrect claims present in token", null, roles);
		}

	}

	private JWTVerifier buildJWTVerifier() throws CertificateException, JwtTokenMalformedException {
		Algorithm algo = Algorithm.RSA256(getRSAPublicKey(), null);
		return JWT.require(algo).build();
	}

	private RSAPublicKey getRSAPublicKey() throws CertificateException, JwtTokenMalformedException {
		byte[] decode = Base64.getDecoder().decode(publicKeyString);
		Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decode));
		RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
		return publicKey;
	}

	private void getPublicKey() throws JwtTokenMalformedException {
		try {
			// Decode the JWT token and extract the "kid" claim
			String kid = getTokenKeyId();
			// Extract the value of x5c for the matching key ID
			publicKeyString = extractX5CValue(kid);
			// When the public key is not found in the Azure AD JWKS, that means Microsoft
			// rotated the public keys. Therefore, re-fetch the keys
			if (publicKeyString == null) {
				fetchPublicJwks();
				publicKeyString = extractX5CValue(kid);
				if (publicKeyString == null) {
					throw new RuntimeException("Matching key ID not found in the Azure AD public key response.");
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new JwtTokenMalformedException("Invalid JWT token");
			// return null;
		}
	}

	private String getTokenKeyId() throws JwtTokenMalformedException {
		// Decode the JWT token and extract the "kid" claim
		String[] parts = token.split("\\.");
		String header = new String(Base64.getUrlDecoder().decode(parts[0]));
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode;
		try {
			jsonNode = mapper.readTree(header);
		} catch (JsonProcessingException e) {
			// e.printStackTrace();
			throw new JwtTokenMalformedException("Invalid JWT token");
		}
		return jsonNode.get("kid").asText();
	}

	private String extractX5CValue(String kid) {
		String result = null;
		JsonNode keysNode = this.jwksNode.get("keys");
		if (keysNode.isArray()) {
			for (JsonNode keyNode : keysNode) {
				String keyId = keyNode.get("kid").asText();
				if (keyId.equals(kid)) {
					JsonNode x5cNode = keyNode.get("x5c");
					if (x5cNode.isArray() && x5cNode.size() == 1) {
						result = x5cNode.get(0).asText();
						break;
					} else {
						throw new RuntimeException("Invalid x5c value in the Azure AD public key response.");
					}
				}
			}
		}
		return result;
	}

	private String getAzureADPublicKey(URL endpointUrl) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			connection.disconnect();
			return response.toString();
		} else {
			throw new RuntimeException("Failed to fetch Azure AD public key. Response code: " + responseCode);
		}
	}

	private void fetchPublicJwks() {
		// Get the public key from Azure AD OpenID configuration end point
		URL endpointUrl;
		try {
			endpointUrl = new URL("https://login.microsoftonline.com/" + tenantId + "/discovery/keys?appid=" + clientApplicationId);
			String publicKeyResponse = getAzureADPublicKey(endpointUrl);
			ObjectMapper mapper = new ObjectMapper();
			this.jwksNode = mapper.readTree(publicKeyResponse);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	
}
