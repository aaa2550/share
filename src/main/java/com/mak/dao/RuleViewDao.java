package com.mak.dao;

import com.mak.dto.RuleView;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

/**
 * Created by yanghailong on 2018/1/19.
 */
@DB(table = "rule_view")
public interface RuleViewDao {

    @SQL("insert into #table(id,date,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10) " +
            "values(:1.id,:1.date,:1.field1,:1.field2,:1.field3,:1.field4,:1.field5,:1.field6,:1.field7,:1.field8,:1.field9,:1.field10)")
    int insert(RuleView ruleView);

    @SQL("insert into #table(id,date,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10) " +
            "values(:1.id,:1.date,:1.field1,:1.field2,:1.field3,:1.field4,:1.field5,:1.field6,:1.field7,:1.field8,:1.field9,:1.field10)")
    int insert(List<RuleView> ruleViews);

}
