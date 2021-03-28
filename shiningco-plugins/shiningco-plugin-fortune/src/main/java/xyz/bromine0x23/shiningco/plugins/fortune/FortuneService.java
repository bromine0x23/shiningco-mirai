package xyz.bromine0x23.shiningco.plugins.fortune;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.GregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.Well512a;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

@Slf4j
@Service
public class FortuneService {

	private static final String[] STEMS_NAMES = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
	private static final String[] BRANCHES_NAMES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
	private static final String[] CHINESE_ZODIAC_NAMES = {
		"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"
	};
	private static final String[] CHINESE_MONTH_NAMES = {
		"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"
	};
	private static final String[] CHINESE_DAY_NAMES    = {
		null,
		"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
		"十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
		"廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十",
	};

	private static final String GREGORIAN_CALENDAR_FORMAT = "y年M月d日 EEEE";

	public String formatGregorianCalendar(Instant instant) {
		var calendar = new GregorianCalendar();
		var format   = DateFormat.getPatternInstance(calendar, GREGORIAN_CALENDAR_FORMAT, Locale.SIMPLIFIED_CHINESE);
		return format.format(Date.from(instant));
	}

	public String formatChineseCalendar(Instant instant) {
		var calendar = new ChineseCalendar();
		calendar.setTime(Date.from(instant));
		var year  = calendar.get(Calendar.YEAR);
		var month = calendar.get(Calendar.MONTH);
		var isLeapMonth = calendar.get(Calendar.IS_LEAP_MONTH);
		var day = calendar.get(Calendar.DAY_OF_MONTH);

		var string = new StringBuilder();

		string.append(STEMS_NAMES[(year - 1) % STEMS_NAMES.length]);
		string.append(BRANCHES_NAMES[(year - 1) % BRANCHES_NAMES.length]);
		string.append(CHINESE_ZODIAC_NAMES[(year - 1) % CHINESE_ZODIAC_NAMES.length]);
		string.append('年');
		if (isLeapMonth != 0) {
			string.append("闰");
		}
		string.append(CHINESE_MONTH_NAMES[month]);
		string.append(CHINESE_DAY_NAMES[day]);

		return string.toString();
	}

	public int fortune(long id) {
		var generator = new Well512a(id);
		return (generator.nextInt(1010 - 9 + 1) + 9) / 10 - 1;
	}

}
