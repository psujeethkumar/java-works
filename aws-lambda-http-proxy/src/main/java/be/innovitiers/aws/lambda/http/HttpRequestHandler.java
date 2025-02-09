package be.innovitiers.aws.lambda.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class HttpRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, HttpResponse<String>> {

	private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(10)).build();

	@Override
	public HttpResponse<String> handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		
		System.out.println("Reached to Java Lambda function");

		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://checkbankaccount-http-cp4i-dev.checkbankaccount.apps.osd-t.rsrc.int/Services/Payment/CheckBankAccount/2.0?wsdl"))
				.setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
				.build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// print response headers
		HttpHeaders headers = response.headers();
		headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

		// print status code
		System.out.println(response.statusCode());

		// print response body
		System.out.println(response.body());

		return response;
	}
}
