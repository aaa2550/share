package com.mak.rule;

import com.mak.dto.RuleView;

import java.util.Map;

/**
 * Created by yanghailong on 2018/1/19.
 */
public class YesterdayUpStopRule extends AbstractRuleChain<String, RuleView> {

    public YesterdayUpStopRule(AbstractRuleChain next) {
        super(next);
    }

    @Override
    protected Map<String, RuleView> execute(Map<String, RuleView> map) {
        return null;
    }
}
