package files;

import java.text.DecimalFormat;
import java.util.TimerTask;

public class TimeCounter extends TimerTask {
	
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private int day = 0;
	private DecimalFormat decFormat = new DecimalFormat("00");
	
	public void run(){
		second++;
		
		if (second == 60)
		{
			
			minute++;
			second = 0;
			
		}
		
		if (minute == 60)
		{
			
			hour++;
			minute = 0;
			
		}
		
		if (hour == 24)
		{
			
			day++;
			hour = 0;
			
		}
		
	}
	
	public TimeCounter()
	{
	}
	
	public void resetTime()
	{
		second = minute = hour = day = 0;
	}
	
	public int getSeconds()
	{
		return second;
	}
	
	public int getMinutes()
	{
		return minute;
	}
	
	public int getHours()
	{
		return hour;
	}
	
	public int getDays()
	{
		return day;
	}
	
	public String getTime()
	{
		return defaultTimeFormat();
	}
	
	private String defaultTimeFormat()
	{
		return "" + decFormat.format(hour) + ":" + decFormat.format(minute) + ":" + decFormat.format(second);
	}
	
	public String getTimeFormat(int format)
	{
		
		String timeFormat = "";
		
		if (format == 0) //Default 3:04:16
		{
			timeFormat = defaultTimeFormat();
		}
		else if (format == 1) //Default 03:17
		{
			timeFormat = "" + decFormat.format(minute) + ":" + decFormat.format(second);
		}
		else if (format == 2)
		{
			timeFormat = "" + decFormat.format(hour) + ":" + decFormat.format(minute);
		}
		else if (format == 3)
		{
			
			if (hour >= 12 && hour < 24)
			{
				timeFormat = "" + decFormat.format((hour - 12)) + ":" + decFormat.format(minute) + ":" + decFormat.format(second) + "pm";
			}
			else
			{
				timeFormat = "" + decFormat.format(hour) + ":" + decFormat.format(minute) + ":" + decFormat.format(second) + "am";
			}
			
		}
		else if (format == 4)
		{
			
			if (hour >= 12 && hour < 24)
			{
				timeFormat = "" + decFormat.format(minute) + ":" + decFormat.format(second) + "pm";
			}
			else
			{
				timeFormat = "" + decFormat.format(minute) + ":" + decFormat.format(second) + "am";
			}
			
		}
		else
		{
			timeFormat = "Error";
		}
		
		return timeFormat;
		
	}		
		
}