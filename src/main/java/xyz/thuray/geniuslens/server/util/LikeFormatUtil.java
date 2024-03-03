package xyz.thuray.geniuslens.server.util;

public class LikeFormatUtil {
    public static String format(String count) {
        Integer likeCount = Integer.parseInt(count);
        if (likeCount < 1000) {
            return String.valueOf(likeCount);
        } else if (likeCount < 10000) {
            return String.format("%.1fK", likeCount / 1000.0);
        } else {
            return String.format("%.1fM", likeCount / 1000000.0);
        }
    }
}
