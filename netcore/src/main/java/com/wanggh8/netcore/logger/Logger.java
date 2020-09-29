package com.wanggh8.netcore.logger;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;

public class Logger {

    static boolean ISLOGGERTOFILE = false;
    static boolean ISLOGGER = true;

    static class Builder {

        public Builder ISLOGGER(boolean flag) {
            ISLOGGER = flag;
            return this;
        }

        public Builder ISLOGGERTOFILE(boolean flag) {
            ISLOGGERTOFILE = flag;
            return this;
        }
    }

    //   String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//   String folder = diskPath + File.separatorChar + "logger";

    private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

    public static void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(3)        // (Optional) Skips some method invokes in stack trace. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("Logger")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
                .build();

        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        saveLogsToFile();

        d("Logger", "Logger init seccuss");
    }

    public static void initLogger(boolean ISLOGGER, boolean ISLOGGERTOFILE) {
        Logger.ISLOGGER = ISLOGGER;
        Logger.ISLOGGERTOFILE = ISLOGGERTOFILE;
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(3)        // (Optional) Skips some method invokes in stack trace. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("Logger")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
                .build();

        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        saveLogsToFile();

        d("Logger", "Logger init seccuss");
    }

    //Save logs to the file
    private static void saveLogsToFile() {
        if (!ISLOGGERTOFILE) {
            return;
        }
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag("FamilyAlbum")
                .build();
        com.orhanobut.logger.Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
    }

    public static void w(String tag, String msg) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.t(tag).w(msg);
        }
    }

    public static void d(String tag, String msg) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.t(tag).d(msg);
        }
    }

    public static void d(String msg) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.d(msg);
        }
    }

    public static void d(Object object) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.d(object);
        }
    }

    public static void i(String tag, String msg) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.t(tag).i(msg);
        }
    }

    public static void v(String tag, String msg) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.t(tag).v(msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.t(tag).e(msg);
        }
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.e(throwable, message, args);
        }
    }

    public static void json(String json) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.json(json);
        }
    }

    public static void xml(String xml) {
        if (ISLOGGER) {
            com.orhanobut.logger.Logger.xml(xml);
        }
    }
}
