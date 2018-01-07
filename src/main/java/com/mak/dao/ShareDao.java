package com.mak.dao;

import com.mak.dto.Share;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

/**
 * Created by lenovo on 2018/1/6.
 */
@DB(table = "share_list")
public interface ShareDao {

    @SQL("insert into #table(code,name,industry,area,pe,outstanding,totals,totalAssets,liquidAssets,fixedAssets,esp,bvps,pb,timeToMarket,undp,holders) " +
            "values(:1.code,:1.name,:1.industry,:1.area,:1.pe,:1.outstanding,:1.totals,:1.totalAssets,:1.liquidAssets,:1.fixedAssets,:1.esp,:1.bvps,:1.pb,:1.timeToMarket,:1.undp,:1.holders)")
    int insert(List<Share> shares);

    @SQL("select code,name from #table")
    List<Share> findAll();

    @SQL("select * from #table where code=:1")
    Share find(String code);
    
}
