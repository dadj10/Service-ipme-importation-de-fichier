package com.hyperaccesss.entities;

import java.io.Serializable;

public class ResponseApi implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Attributs
	private int status;
	private String url;

	public ResponseApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseApi(int status, String url) {
		super();
		this.status = status;
		this.url = url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
