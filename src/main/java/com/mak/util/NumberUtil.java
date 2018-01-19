package com.mak.util;

import java.math.BigDecimal;

/**
 * Created by yanghailong on 2018/1/19.
 */
public class NumberUtil {

    public static double percent(Object all, Object use) {
        BigDecimal allBigDecimal = new BigDecimal(all.toString());
        BigDecimal useBigDecimal = new BigDecimal(use.toString());
        return useBigDecimal.divide(allBigDecimal, 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100)).doubleValue();
    }

}
