package rester.tools;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import rester.model.Proxy;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class UTIL {
    public static Optional<Proxy> uphp(String pStr) {
        if (StringUtils.isBlank(pStr)) {
            return Optional.empty();
        }
        pStr = pStr.trim();
        String up = "";
        String hp = "";
        if (pStr.contains("@")) {
            up = pStr.split("@")[0];
            String hpt = pStr.split("@")[1];
            if (hpt.contains(":")) {
                hp = hpt;
            }
        } else {
            if (pStr.contains(":")) {
                hp = pStr;
            }
        }
        if (StringUtils.isNotBlank(hp)) {
            String h = hp.split(":")[0];
            String p = hp.split(":")[1];
            if (StringUtils.isNotBlank(h) && StringUtils.isNotBlank(p)) {
                if (StringUtils.isNotBlank(up) && up.contains(":")) {
                    String u = up.split(":")[0];
                    String pwd = up.split(":")[1];
                    if (StringUtils.isNotBlank(u) && StringUtils.isNotBlank(pwd)) {
                        return Optional
                            .of(new Proxy(decode(h.trim()), decode(p.trim()), decode(u.trim()), decode(pwd.trim())));
                    }
                } else {
                    return Optional.of(new Proxy(decode(h.trim()), decode(p.trim())));
                }
            }
        }
        return Optional.empty();
    }

    public static String decode(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
    public static ThreadPoolExecutor threadPool(int size,String name) {
        return new ThreadPoolExecutor(size, size, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat(name).setPriority(Thread.MAX_PRIORITY).build());
    }
}
