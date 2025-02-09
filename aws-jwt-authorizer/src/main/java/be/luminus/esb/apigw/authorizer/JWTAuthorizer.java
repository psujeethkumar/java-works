package be.luminus.esb.apigw.authorizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.MissingClaimException;

import be.luminus.esb.apigw.exceptions.JwtTokenMalformedException;
import be.luminus.esb.apigw.exceptions.JwtTokenMissingException;
import be.luminus.esb.apigw.jwt.JwtTokenVerifier;
import be.luminus.esb.apigw.models.AuthorizerResponse;
import be.luminus.esb.apigw.models.PolicyDocument;
import be.luminus.esb.apigw.models.Statement;

public class JWTAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, AuthorizerResponse> {

	private JwtTokenVerifier tokenVerifier = null;

	public JWTAuthorizer() {
		tokenVerifier = new JwtTokenVerifier();
	}

	public AuthorizerResponse handleRequest(APIGatewayProxyRequestEvent request, Context context) {

		Map<String, String> ctx = new HashMap<>();
		String effect = "Allow";

		APIGatewayProxyRequestEvent.ProxyRequestContext proxyContext = request.getRequestContext();
		String arn = String.format("arn:aws:execute-api:%s:%s:%s/%s/%s/%s", System.getenv("AWS_REGION"), proxyContext.getAccountId(), proxyContext.getApiId(), proxyContext.getStage(), proxyContext.getHttpMethod(), "*");

		Map<String, String> headers = request.getHeaders();
		String token = headers.get("Authorization");

		try {
			if (token == null || token.trim().length() == 0) {
				throw new JwtTokenMissingException("Token is missing in the request");
			} else {

				if (token.contains("Bearer")) {
					token = token.substring(token.lastIndexOf("Bearer") + 6).trim();
				}

				tokenVerifier.validateToken(token);
				ctx.put("message", "Success");
			}
		} catch (JwtTokenMalformedException | JwtTokenMissingException | MissingClaimException | IncorrectClaimException e) {
			effect = "Deny";
			System.out.println(e.getMessage());
			ctx.put("message", e.getMessage());
		}
		Statement statement = Statement.builder().resource(arn).effect(effect).build();
		PolicyDocument policyDocument = PolicyDocument.builder().statements(Collections.singletonList(statement)).build();
		AuthorizerResponse response = AuthorizerResponse.builder().principalId(proxyContext.getAccountId()).policyDocument(policyDocument).context(ctx).build();
		return response;
	}
}