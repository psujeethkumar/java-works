package be.innovitiers.datetime.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CountCalculator {

	public static void main(String[] args) {

		ZonedDateTime startDate = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault());
		ZonedDateTime endDate = ZonedDateTime.now().toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault());

		System.out.println("Start Date Time : " + startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		long dailyMinutes = ChronoUnit.MINUTES.between(startDate, endDate);
		for (int minute = 0; minute < dailyMinutes; minute += 15) {
			System.out.println("Time : " + startDate.plusMinutes(minute).toInstant() + " <---> Quarter Hour Number : "
					+ new CountCalculator().calculateQuarterHourCount(Date.from(startDate.plusMinutes(minute).toInstant())));
		}
		System.out.println("End Date Time : " + endDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
	}

	/**
	 * Below method takes Date as input and provides corresponding quarter hour
	 * of day
	 * 
	 * @param inputDate
	 * @return
	 */
	public int calculateQuarterHourCount(Date inputDate) {
		int quarterHourCount = 0;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(inputDate);
		quarterHourCount += calendar.get(Calendar.HOUR_OF_DAY) * 4;
		quarterHourCount += (15 * (calendar.get(Calendar.MINUTE) / 15)) / 15 + 1;
		return quarterHourCount;
	}
}
