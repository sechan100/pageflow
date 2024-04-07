package org.pageflow.shared.utils;

/**
 * @author : sechan
 */
public abstract class TimeIntorducer {

    public static class MilliSeconds {
        public static final long SECOND = 1000L;
        public static final long MINUTE = SECOND * 60L;
        public static final long HOUR = MINUTE * 60L;
        public static final long DAY = HOUR * 24L;
        public static final long WEEK = DAY * 7L;
        public static final long MONTH = DAY * 30L;
        public static final long YEAR = DAY * 365L;
    }

    public static class Seconds {
        public static final int MINUTE = 60;
        public static final int HOUR = MINUTE * 60;
        public static final int DAY = HOUR * 24;
        public static final int WEEK = DAY * 7;
        public static final int MONTH = DAY * 30;
        public static final int YEAR = DAY * 365;
    }
}
