package com.hyperaccesss.config;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class ServeurUtility {

	public static Properties getPop3ServerProperties(String protocol, String serverHost, String serverPort) {
		Properties properties = new Properties();

		// Je définis les propriétés du serveur Pop3.
		properties.put("mail.store.protocol", protocol);
		properties.put("mail.pop3.host", serverHost);
		properties.put("mail.pop3.port", serverPort);
		properties.put("mail.pop3.ssl.trust", "true");

		return properties;
	}

	public static Properties getPopServerProperties(String protocol, String serverHost, String serverPort) {
		Properties properties = new Properties();

		// Je définis les propriétés du serveur Pop.
		properties.put("mail.smtp.host", serverHost);
		properties.put("mail.smtp.port", serverPort);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.trust", serverHost);

		return properties;
	}

	public static Authenticator authenticator(String serverHost, String serverPassword) {
		// Je crée une nouvelle session avec un authentificateur.
		Authenticator auth = new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(serverHost, serverPassword);
			}
		};
		return auth;
	}

	public static Properties getImapServerProperties(String protocol) {
		Properties properties = new Properties();

		// Je définis les propriétés du serveur Imap.
		properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imap.socketFactory.fallback", "false");
		properties.setProperty("mail.store.protocol", protocol);
		properties.setProperty("mail.debug", "true");

		return properties;
	}

	// Je récupère les propietes du serveur exchange.
	public static Properties getExchangeServerProperties() {
		Properties properties = new Properties();

		// Je définis les propriétés du serveur.
		properties.setProperty("mail.store.protocol", "imaps"); // protocol imaps

		return properties;
	}

}
