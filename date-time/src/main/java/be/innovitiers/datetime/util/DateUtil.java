package be.innovitiers.datetime.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;


public class DateUtil {

	public static void main(String[] args) {
		DateUtil dateUtil = new DateUtil();
		dateUtil.convertToUtc("2020-03-28T23:00:00Z");
//		System.out.println("Answer: " +dateUtil.calculateQuarterHourCount(dateUtil.convertToUtc("2020-03-29T22:00:00Z")));
//		System.out.println("Answer: " +dateUtil.calculateQuarterHourCount(dateUtil.convertToUtc("2020-03-30T21:45:00Z")));
	}

	private Date convertToUtc(String inputDate) {
		Calendar calendar = DatatypeConverter.parseDateTime(inputDate);

		System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
		
		Calendar time = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		time.setTime(calendar.getTime());
		time.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		time.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		time.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
		time.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
		time.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
		time.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
		calendar = Calendar.getInstance();
		calendar.setTime(time.getTime());
		System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
		//System.out.println(time.get(Calendar.HOUR_OF_DAY));

//		Calendar gc = GregorianCalendar.getInstance();
//		gc.setTime(calendar.getTime());
		//System.out.println(gc.get(Calendar.HOUR_OF_DAY));
		
		
		//System.out.println("Start");
		int quarterHourCount = 0;
		quarterHourCount += calendar.get(Calendar.HOUR_OF_DAY) * 4;
		quarterHourCount += (15 * (calendar.get(Calendar.MINUTE) / 15)) / 15 + 1;

		//System.out.println(quarterHourCount);
		return calendar.getTime();
		
		
		/**
		 * Calendar gcalendar = GregorianCalendar.getInstance();
		gcalendar.setTime(time.getTime());
		System.out.println(gcalendar.get(Calendar.HOUR_OF_DAY));

		int quarterHourCount = 0;
		quarterHourCount += gcalendar.get(Calendar.HOUR_OF_DAY) * 4;
		quarterHourCount += (15 * (gcalendar.get(Calendar.MINUTE) / 15)) / 15 + 1;
		System.out.println(quarterHourCount);
		 */
				
	}

	public int calculateQuarterHourCount(Date inputDate) {
		int quarterHourCount = 0;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(inputDate);
		quarterHourCount += calendar.get(Calendar.HOUR_OF_DAY) * 4;
		quarterHourCount += (15 * (calendar.get(Calendar.MINUTE) / 15)) / 15 + 1;
		return quarterHourCount;
	}

}
