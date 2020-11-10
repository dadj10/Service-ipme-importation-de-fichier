package com.hyperaccesss.entities;

import java.io.Serializable;

public class ResponseApi implements Serializable {

	private static final long serialVersionUID = 1L;

	// Attributs
	private int status;
	private String url;
	private String info;

	// Constructeur sans params
	public ResponseApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Constructeur avec params
	public ResponseApi(int status, String url, String info) {
		super();
		this.status = status;
		this.url = url;
		this.info = info;
	}

	// Getters & Setters
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
