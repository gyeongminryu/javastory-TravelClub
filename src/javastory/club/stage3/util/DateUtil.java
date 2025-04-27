package javastory.club.stage3.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

	//씀
	public static String today() {
		return today("yyyy.MM.dd");
	}


	//이건 아직 안씀
	public static String today(String format) { //format 안씀
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd"); 
		return dateFormat.format((Calendar.getInstance()).getTime()); 
	}
	

}