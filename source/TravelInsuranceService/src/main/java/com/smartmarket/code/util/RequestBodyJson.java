package com.smartmarket.code.util;

import org.springframework.stereotype.Component;

@Component
public class RequestBodyJson {
	private String bodyString = "";

	public String getBodyString() {
		return bodyString;
	}

	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	public RequestBodyJson() {
		super();
	}

	public EJson JSONParse(String source) {
		if (bodyString.equals("")) {
			this.bodyString = source;
		}

		return new EJson(source);
	}
}
