package xyz.thuray.geniuslens.server.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class TimeFormatUtil {
    private static final String zone = "+8";
    private static final ZoneOffset zoneOffset = ZoneOffset.of(zone);

    // 使用一个Map来存储时间格式化的方法
    // key为距离当前时间的秒数，value为格式化时间的方法
    // 这样可以根据时间的不同距离，使用不同的格式化方法
    // 例如，当时间距离当前时间小于1分钟时，使用“刚刚”来表示
    // 当时间距离当前时间大于1分钟小于1小时时，使用“x分钟前”来表示
    // 当时间距离当前时间大于1小时小于1天时，使用“x小时前”来表示
    // 当时间距离当前时间大于1天小于1周时，使用“x天前”来表示
    // 当时间距离当前时间大于1周时，使用“yyyy-MM-dd”来表示
    private static final Map<Long, Function<LocalDateTime, String>> timeFormatMap = new HashMap<>();

    static {
        // 一分钟
        timeFormatMap.put(60L, time -> "刚刚");
        // 一小时
        timeFormatMap.put(3600L, TimeFormatUtil::computeMinutes);
        // 一天
        timeFormatMap.put(86400L, TimeFormatUtil::computeHours);
        // 一周
        timeFormatMap.put(604800L, TimeFormatUtil::computeDays);
        // 其他
        timeFormatMap.put(Long.MAX_VALUE, time -> time.toLocalDate().toString());
    }

    public static String format(LocalDateTime time) {
        log.info("time: {}", time);
        long seconds = LocalDateTime.now().toEpochSecond(zoneOffset) - time.toEpochSecond(zoneOffset);
        log.info("seconds: {}", seconds);
        // 按顺序遍历timeFormatMap，找到第一个key大于seconds的entry，然后使用其value来格式化时间
        Long closest = timeFormatMap.keySet().stream().filter(aLong -> aLong > seconds).sorted().findFirst().orElse(Long.MAX_VALUE);
        return timeFormatMap.get(closest).apply(time);
    }

    private static String computeMinutes(LocalDateTime time) {
        // 先检查是否在同一小时内，否则要在取出分钟后+60
        int nowMinute = LocalDateTime.now().toLocalTime().getMinute();
        int timeMinute = time.toLocalTime().getMinute();
        if (nowMinute > timeMinute) {
            return (nowMinute - timeMinute) + "分钟前";
        } else {
            return (nowMinute + 60 - timeMinute) + "分钟前";
        }
    }

    private static String computeHours(LocalDateTime time) {
        // 同理，要检查是否在同一天内
        int nowHour = LocalDateTime.now().toLocalTime().getHour();
        int timeHour = time.toLocalTime().getHour();
        if (nowHour > timeHour) {
            return (nowHour - timeHour) + "小时前";
        } else {
            return (nowHour + 24 - timeHour) + "小时前";
        }
    }

    private static String computeDays(LocalDateTime time) {
        // 同理，要检查是否在同一月内
        int nowDay = LocalDateTime.now().toLocalDate().getDayOfMonth();
        int timeDay = time.toLocalDate().getDayOfMonth();
        if (nowDay > timeDay) {
            return (nowDay - timeDay) + "天前";
        } else {
            return (nowDay + 30 - timeDay) + "天前";
        }
    }
}
