package edu.pa.web.prts.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */
public class DBUtil {

    private DBUtil() { }

    public static <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T element : iterable) {
            list.add(element);
        }
        return list;
    }
}
