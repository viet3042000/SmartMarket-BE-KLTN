package com.smartmarket.code.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ResponseBodyJson {
	private String bodyString = "";

	public String getBodyString() {
		return bodyString;
	}

	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	public ResponseBodyJson() {
		super();
	}

	public EJson newEJson() {
		return new EJson(this);
	}
}
