package com.hyperaccesss.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

	public static String dateNow() {
		String dateNow = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		dateNow = sdf.format(date);
		return dateNow;
	}

	// Augmenter une date jour par jour
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	// Augmenter une date jour par jour
	public static Date addMinutes(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minute); // minus number would decrement the minute
		return cal.getTime();
	}

	public Date addMonth(Date date, int minute) {
		Date now = date;
		Calendar myCal = Calendar.getInstance();
		myCal.setTime(now);
		myCal.add(Calendar.MONTH, +minute);
		now = myCal.getTime();
		System.out.println("===========> " + now);
		return now;
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String formatNumero(String expediteur) {
		System.out.println("Est numérique : " + isNumeric(expediteur));

		// Mes declarations
		String numero = null;
		String indicatif = null;
		int taille = 0;

		// Je recupère la taille du numéro expéditeur
		taille = expediteur.length();

		if (isNumeric(expediteur) == true) {
			// Si le numéro existe alors
			if (taille > 0) {

				// Je recupère le l'indicatif pays +225
				indicatif = expediteur.substring(0, 4);

				// Si la taille du numéro égale à 12 caractères alors
				if (taille == 11) {
					// Si l'indicatif est +225 alors
					if (indicatif.equalsIgnoreCase("225")) {
						numero = expediteur;
					}
				} else if (taille == 12) {
					// Si l'indicatif est +225 alors
					if (indicatif.equalsIgnoreCase("+225")) {
						numero = expediteur.substring(1, 12);
					}
				} else if (taille == 13) {
					// Si l'indicatif est +225 alors
					if (indicatif.equalsIgnoreCase("225")) {
						numero = expediteur;
					}
				} else if (taille == 14) {
					// Si l'indicatif est +225 alors
					if (indicatif.equalsIgnoreCase("+225")) {
						numero = expediteur.substring(1, 14);
					}
				} else {
					numero = expediteur;
				}
			}
		}

		return numero;
	}

	public static Long dateMinuteDifference(String date1, String date2, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		
		Date d1 = sdf.parse(date1);
		Date d2 = sdf.parse(date2);
		
		Long diffInMillis = d2.getTime() - d1.getTime();
		Long dateDiffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

		Long dateDiffInHours = TimeUnit.HOURS.convert(diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		Long dateDiffInMinutes = TimeUnit.MINUTES.convert(
				diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000) - (dateDiffInHours * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		Long dateDiffInSeconds = TimeUnit.SECONDS.convert(diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000)
				- (dateDiffInHours * 60 * 60 * 1000) - (dateDiffInMinutes * 60 * 1000), TimeUnit.MILLISECONDS);

		//System.out.println(dateDiffInDays + " day(s) " + dateDiffInHours + " Hour(s) " + dateDiffInMinutes
			// 	+ " Minute(s) " + dateDiffInSeconds + " Second(s)");
		
		return dateDiffInSeconds;	
		
	}
	
	public static Long differenceEnMinute (Date startDate, Date endDate) {
		// Date startDate = sentDate;// Set start date
		// Date endDate = new Date(); // Set end date

		Long duration = endDate.getTime() - startDate.getTime();
		
		// Long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		Long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		// Long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		// Long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
		
		return diffInMinutes;
	}

}
