package xyz.bromine0x23.shiningco.services;

import org.springframework.stereotype.Service;
import xyz.bromine0x23.shiningco.runtime.WorkTimeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultWorkTimeService implements WorkTimeService {

	private static final LocalTime WORK_TIME_LOWER = LocalTime.of(8, 0);
	private static final LocalTime WORK_TIME_UPPER = LocalTime.of(20, 0);

	private static final Set<DayOfWeek> WEEKENDS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

	private static final Set<LocalDate> HOLIDAYS = toLocalDates(
		"2021-01-01", "2021-01-02", "2021-01-03",
		"2021-02-11", "2021-02-12", "2021-02-13", "2021-02-14", "2021-02-15", "2021-02-16", "2021-02-17",
		"2021-04-03", "2021-04-04", "2021-04-05",
		"2021-05-01", "2021-05-02", "2021-05-03", "2021-05-04", "2021-05-05",
		"2021-06-12", "2021-06-13", "2021-06-14",
		"2021-09-19", "2021-09-21", "2021-09-21",
		"2021-10-01", "2021-10-02", "2021-10-03", "2021-10-04", "2021-10-05", "2021-10-06", "2021-10-07",
		"2022-01-01", "2022-01-02", "2022-01-03"
	);

	private static final Set<LocalDate> WORKDAYS = toLocalDates(
		"2021-02-07", "2021-02-20",
		"2021-04-25", "2021-05-08",
		"2021-09-18",
		"2021-09-26", "2021-10-09"
	);

	@Override
	public boolean duringWorkTime() {
		var dateTime = LocalDateTime.now();
		var date = dateTime.toLocalDate();
		var time = dateTime.toLocalTime();
		if (isHoliday(date) || !isWorkday(date) && isWeekend(date)) {
			return false;
		}
		return isWorkTime(time);
	}

	private static boolean isWeekend(LocalDate date) {
		return WEEKENDS.contains(date.getDayOfWeek());
	}

	private static boolean isHoliday(LocalDate date) {
		return HOLIDAYS.contains(date);
	}

	private static boolean isWorkday(LocalDate date) {
		return WORKDAYS.contains(date);
	}

	private static boolean isWorkTime(LocalTime time) {
		return WORK_TIME_LOWER.isBefore(time) && WORK_TIME_UPPER.isAfter(time);
	}

	private static Set<LocalDate> toLocalDates(String ... dates) {
		return Arrays.stream(dates)
			.map(LocalDate::parse)
			.collect(Collectors.toUnmodifiableSet());
	}

}
