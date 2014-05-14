package wp.core;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Constants {

	public static final DecimalFormat		COST_FORMAT = new DecimalFormat("#.##");
	public static final SimpleDateFormat	DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy hh:MM");
	public static final SimpleDateFormat	PRECISE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
	public static final DecimalFormat		SMALL_NUM_FORMAT = new DecimalFormat("#.######");
}
