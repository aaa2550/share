package com.mak.rule;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.core.Ordered;

import java.util.Map;

/**
 * Created by yanghailong on 2018/1/19.
 */
public abstract class AbstractRuleChain<K, V> {

    private AbstractRuleChain next;

    public AbstractRuleChain(AbstractRuleChain next) {
        this.next = next;
    }

    protected abstract Map<K, V> execute(Map<K, V> map);

}
