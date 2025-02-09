package com.innovitiers.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloLambda implements RequestHandler<String, String> {

	@Override
	public String handleRequest(String input, Context context) {
		System.out.println("Hello : " + input);
		return "DONE";
	}

}
