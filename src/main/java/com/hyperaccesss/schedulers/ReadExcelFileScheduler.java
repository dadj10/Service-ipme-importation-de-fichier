package com.hyperaccesss.schedulers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.hyperaccesss.config.Utils;
import com.hyperaccesss.entities.ResponseApi;

@Component
public class ReadExcelFileScheduler {

	@Value("${read.directory}")
	private String readDirectory;

	@Value("${csv.extention}")
	private String csvExtention;

	@Value("${xlsx.extention}")
	private String xlsxExtention;

	@Value("${serverHost}")
	private String serverHost;

	@Value("${serverPort}")
	private int serverPort;

	@Value("${serverPassword}")
	private String serverPassword;

	@Value("${serverEmail}")
	private String serverEmail;

	// Logger.
	private static final Logger log = LoggerFactory.getLogger(ReadExcelFileScheduler.class);

	@Scheduled(fixedDelay = 10000)
	public void CSVFileReader() {

		System.out.println(Utils.dateNow() + " Run CSVFileReader...");

		// Creating a File object for directory.
		File directoryPath = new File(readDirectory);

		// List of all files and directories.
		File listFiles[] = directoryPath.listFiles();

		// Je parcours la liste des fichiers trouvés.
		for (File file : listFiles) {

			String filename = null;
			filename = file.getName().toLowerCase().trim();
			System.out.println("fileName = " + filename);

			List<String> allLines = null;
			// Si le fichier correspond a l'extention, alors...
			if (filename.endsWith(csvExtention)) {

				try {
					// Je récupère toutes les lignes contenus dans le fichier en une liste.
					allLines = Files.readAllLines(Paths.get(file.toString()));

					// Si le liste de ligne n'est pas vide alors...
					if (allLines != null) {

						// Je parcours la liste de ligne.
						for (String line : allLines) {
							// Je récupère l'index de la ligne.
							int index;
							index = allLines.indexOf(line);
							System.out.println("index = " + index);

							// J'ignore la premiere ligne du fichier correspondant à l'entete (index = 0).
							if (index != 0) {
								System.out.println("ligne = " + line);

								// Je fais un split la line recuperée par le délimiteur.
								String delimiteur = ";";
								String[] parts = line.split(delimiteur);

								// J'initialise les variables a récupérer
								String nom = null;
								String email = null;
								String contact = null;

								if (parts != null) {

									// Je récupère les informations de la ligne.
									nom = parts[0].trim().replace(" ", "%20");
									email = parts[1].trim().replace(" ", "%20");
									contact = parts[2].trim().replace(" ", "%20");

									// Si l'email n'est pas null alors...
									if (email != null) {

										// Je verifier la validité de l'adresse email.
										if (validateEmailAdress(email)) {

											// Je paramètre l'url du webService.
											String api = "http://identificationpme.com/api/encrypt_email?nom=[NOM]&email=[EMAIL]&contact=[CONTACT]";

											// Je format l'url du webService.
											api = api.replace("[NOM]", nom);
											api = api.replace("[EMAIL]", email);
											api = api.replace("[CONTACT]", contact);

											// J'exécute la requète Http via la methode GET.
											HttpURLConnection connection = (HttpURLConnection) new URL(api)
													.openConnection();

											connection.setRequestMethod("GET");
											connection.setRequestProperty("User-Agent", "Mozilla/5.0");

											// Je récupère la reponse (responseCode) de la requète.
											int responseCode = connection.getResponseCode();
											System.out.println("Response Code = " + responseCode);

											// Si la requète Http a été exécutée avec succès.
											if (responseCode == 200) {

												// Je récupère la response retournée par la requète.
												InputStreamReader reader = new InputStreamReader(
														connection.getInputStream());
												BufferedReader br = new BufferedReader(reader);

												String inputLine;
												StringBuffer response = new StringBuffer();
												while ((inputLine = br.readLine()) != null) {
													response.append(inputLine);
												}
												br.close();
												System.out.println("response = " + response.toString());

												// Je crée une instance de ResponseApi().
												ResponseApi responseApi = new ResponseApi();

												// Je crée une instance de Gson().
												Gson gson = new Gson();

												// Je serialize la response avec l'entité ResponseApi.
												responseApi = gson.fromJson(response.toString(), ResponseApi.class);

												System.out.println("status = " + responseApi.getStatus());
												System.out.println("url = " + responseApi.getUrl());

												if (responseApi.getStatus() == 1) {
													// URL d'activation de compte.
													String url = responseApi.getUrl().trim();

													/*
													 * J'envoi l'email au requerant contenant l'url d'activation de
													 * compte.
													 */
													sendEmailToRequerant(email, url);
												}
											}
										}
									}
								}
							}
						}
					}
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					System.err.println(Utils.dateNow() + " Impossible de trouver le fichier: " + file.toString());
				} catch (IOException ex) {
					ex.printStackTrace();
					System.err.println(Utils.dateNow() + " Impossible de lire le fichier: " + file.toString());
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Scheduled(fixedDelay = 10000)
	public void XLSXFileReader() {

		System.out.println(Utils.dateNow() + " Run XLSXFileReader...");

		// Creating a File object for directory.
		File directoryPath = new File(readDirectory);

		// List of all files and directories.
		File listFiles[] = directoryPath.listFiles();

		// Je parcours la liste des fichiers trouvés.
		for (File file : listFiles) {

			String filename = null;
			filename = file.getName().toLowerCase().trim();
			System.out.println("fileName = " + filename);

			System.out.println(file.toString());

			// Si le fichier correspond a l'extention, alors...
			if (filename.endsWith(xlsxExtention)) {

				try {
					FileInputStream stream = new FileInputStream(file);

					// Finds the workbook instance for XLSX file
					XSSFWorkbook workbook = new XSSFWorkbook(stream);

					// Return first sheet from the XLSX workbook
					XSSFSheet sheet = workbook.getSheetAt(0);

					// Get iterator to all the rows in current sheet
					Iterator<Row> rowIterator = sheet.iterator();

					// Traversing over each row of XLSX file
					while (rowIterator.hasNext()) {
						Row row = rowIterator.next();

						// Je récupère l'index de la ligne.
						int index;
						index = row.getRowNum();
						System.out.println("index = " + index);

						// J'ignore la premiere ligne du fichier correspondant à l'entete (index = 0).
						if (index != 0) {

							// For each row, iterate through each columns
							Iterator<Cell> cellIterator = row.cellIterator();

							// J'initialise la ligne.
							String line = "";
							while (cellIterator.hasNext()) {
								Cell cell = cellIterator.next();
								switch (cell.getCellType()) {
								case Cell.CELL_TYPE_STRING:
									System.out.print(cell.getStringCellValue() + "\t");
									line = line + cell.getStringCellValue().toString().trim() + ";";
									break;
								case Cell.CELL_TYPE_NUMERIC:
									System.out.print(cell.getNumericCellValue() + "\t");
									line = line + cell.getStringCellValue().toString().trim() + ";";
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									System.out.print(cell.getBooleanCellValue() + "\t");
									line = line + cell.getStringCellValue().toString().trim() + ";";
									break;
								default:
								}
							}

							System.out.println("ligne = " + line);

							int lineLength = line.length();

							if (lineLength != 0) {

								// Je fais un split la line recuperée par le délimiteur.
								String delimiteur = ";";
								String[] parts = line.split(delimiteur);

								// J'initialise les variables a récupérer
								String nom = null;
								String email = null;
								String contact = null;

								// Je récupère les informations de la ligne.
								nom = parts[0].trim().replace(" ", "%20");
								email = parts[1].trim().replace(" ", "%20");
								contact = parts[2].trim().replace(" ", "%20");

								// Si l'email n'est pas null alors...
								if (email != null) {

									// Je verifier la validité de l'adresse email.
									if (validateEmailAdress(email)) {

										// Je paramètre l'url du webService.
										String api = "http://identificationpme.com/api/encrypt_email?nom=[NOM]&email=[EMAIL]&contact=[CONTACT]";

										// Je format l'url du webService.
										api = api.replace("[NOM]", nom);
										api = api.replace("[EMAIL]", email);
										api = api.replace("[CONTACT]", contact);

										// J'exécute la requète Http via la methode GET.
										HttpURLConnection connection = (HttpURLConnection) new URL(api)
												.openConnection();

										connection.setRequestMethod("GET");
										connection.setRequestProperty("User-Agent", "Mozilla/5.0");

										// Je récupère la reponse (responseCode) de la requète.
										int responseCode = connection.getResponseCode();
										System.out.println("Response Code = " + responseCode);

										// Si la requète Http a été exécutée avec succès.
										if (responseCode == 200) {

											// Je récupère la response retournée par la requète.
											InputStreamReader reader = new InputStreamReader(
													connection.getInputStream());
											BufferedReader br = new BufferedReader(reader);

											String inputLine;
											StringBuffer response = new StringBuffer();
											while ((inputLine = br.readLine()) != null) {
												response.append(inputLine);
											}
											br.close();
											System.out.println("response = " + response.toString());

											// Je crée une instance de ResponseApi().
											ResponseApi responseApi = new ResponseApi();

											// Je crée une instance de Gson().
											Gson gson = new Gson();

											// Je serialize la response avec l'entité ResponseApi.
											responseApi = gson.fromJson(response.toString(), ResponseApi.class);

											System.out.println("status = " + responseApi.getStatus());
											System.out.println("url = " + responseApi.getUrl());

											if (responseApi.getStatus() == 1) {
												// URL d'activation de compte.
												String url = responseApi.getUrl().trim();

												/*
												 * J'envoi l'email au requerant contenant l'url d'activation de compte.
												 */
												sendEmailToRequerant(email, url);
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/*
	 * La fonction sendEmailToRequerant() permet d'envoyer le mail contenant le lien
	 * d'active de compte aux réquérants.
	 */
	public void sendEmailToRequerant(String toAddress, String url) {

		Properties properties = null;
		Authenticator auth = null;
		Session session = null;

		/*
		 * String from = "no-reply@identificationpmeci.com"; String serverHost =
		 * "SSL0.OVH.NET"; int serverPort = 587; String serverPassword =
		 * "mamanMOMO1977";
		 */

		// Je récupère les proprités du serveur.
		properties = getServerProperties(serverHost, serverPort);

		// creates a new session with an authenticator.
		auth = authenticator(serverEmail, serverPassword);

		// Je récupère l'instance de la session.
		session = Session.getInstance(properties, auth);

		try {
			MimeMessage message = new MimeMessage(session);

			message.setSubject("Création de compte requérant");
			message.setHeader("Test", url);

			message.setFrom(new InternetAddress(serverEmail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));

			// message.setText(url);
			message.setContent(url, "text/html");

			// J'envois le message.
			Transport.send(message);
			System.out.println(Utils.dateNow() + " Email envoyé au réquérant " + toAddress);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	/*
	 * La fonction getServerProperties() permet de récupérer les propriétés du
	 * serveur de messagerie.
	 */
	public static Properties getServerProperties(String serverHost, int serverPort) {
		Properties properties = new Properties();

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

	/*
	 * La fonction validateEmailAdress() permet de verifier la validité de l'adresse
	 * email.
	 */
	public static boolean validateEmailAdress(String emailAdress) {
		boolean reponse = true;
		try {
			InternetAddress address = new InternetAddress(emailAdress);
			address.validate();
		} catch (AddressException ex) {
			reponse = false;
			ex.printStackTrace();
		}
		return reponse;
	}
}
