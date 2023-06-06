package mytool.collector.util; /**
 * All right is from Author of the file.
 * Any usage of the code must be authorized by the the auther.
 * If not sure the detail of the license,please distroy the copies immediately.  
 * Nov 19, 2012
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author wuzhen
 * 
 */
public class DateUtil {
	private static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public static String format(Date dd) {
		return FORMAT.format(dd);
	}

	public static Date parse(String date) {
		Date dt = null;
		try {
			dt = FORMAT.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);//
		} //
		return dt;
	}

	public static int getYear(Date date, TimeZone zone) {
		Calendar c = Calendar.getInstance(zone);
		c.setTime(date);

		return c.get(Calendar.YEAR);
	}

	public static Date newDateOfYearLastDay(int year, TimeZone zone) {
		Calendar c = Calendar.getInstance(zone);
		c.set(year, 11, 31, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);//
		return c.getTime();
	}
	public static Date floorHour(Date date, TimeZone zone) {
		Calendar c = Calendar.getInstance(zone);
		c.setTimeInMillis(date.getTime());
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}
	public static Date newDate(int year, int month, int day, TimeZone zone) {
		Calendar c = Calendar.getInstance(zone);
		c.set(year, month, day, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);//
		return c.getTime();
	}
}
