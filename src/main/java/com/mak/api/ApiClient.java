package com.mak.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mak.dto.*;
import com.mak.http.HttpUtil;
import com.mak.http.ProxyPool;
import com.mak.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * Created by lenovo on 2018/1/6.
 */
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private static int timeout = 1;
    private static volatile boolean useProxy;
    private static volatile boolean lock;

    public static List<ProxyInfo> proxyList(String url) {
        String html = HttpUtil.getIntance().get(url);
        html = html.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");
        html = html.substring(html.indexOf("<tbody>") + "<tbody>".length(), html.lastIndexOf("<tr><tdcolspan=\"7\">"));
        List<ProxyInfo> proxyInfos = new ArrayList<>();
        while (html.contains("<tr>")) {
            String singeHtml = html.substring(html.indexOf("<tr>") + "<tr>".length(), html.indexOf("</tr>") + "</tr>".length());
            html = html.substring(html.indexOf(singeHtml) + singeHtml.length());
            proxyInfos.add(parser(singeHtml));
        }
        return proxyInfos;
    }

    public static List<ProxyInfo> proxyListAli(String url) {
        String html = HttpUtil.getIntance().get(url);
        html = html.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");
        html = html.substring(html.indexOf("<tbody>") + "<tbody>".length(), html.lastIndexOf("<tr><tdcolspan=\"7\">"));
        List<ProxyInfo> proxyInfos = new ArrayList<>();
        while (html.contains("<tr>")) {
            String singeHtml = html.substring(html.indexOf("<tr>") + "<tr>".length(), html.indexOf("</tr>") + "</tr>".length());
            html = html.substring(html.indexOf(singeHtml) + singeHtml.length());
            proxyInfos.add(parser(singeHtml));
        }
        return proxyInfos;
    }

    private static ProxyInfo parser(String html) {
        ProxyInfo proxyInfo = new ProxyInfo();
        proxyInfo.setIp(html.substring(html.indexOf("<tddata-title=\"IP\">") + "<tddata-title=\"IP\">".length(), html.indexOf("</td><tddata-title=\"PORT\">")));
        proxyInfo.setPort(html.substring(html.indexOf("<tddata-title=\"PORT\">") + "<tddata-title=\"PORT\">".length(), html.indexOf("</td><tddata-title=\"匿名度\">")));
        return proxyInfo;
    }

    public static List<Share> sharesList(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "GBK"));
            return bufferedReader.lines().skip(1).map(l -> toShare(l.split(","))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<ShareDayDetail> shareDayDetail(String urlStr, String code, String name, Date date) {
            /*while (true) {
                URL url = new URL(urlStr);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openConnection(ProxyPool.getProxyPool().randomProxy()).getInputStream(), "GBK"));
                return bufferedReader.lines().parallel().filter(s->!s.startsWith("alert")).skip(1).map(l->toShareSingeDayDetail(l.split("\t"), code, name, date)).collect(Collectors.toList());
            }*/
        ProxyInfo proxyInfo = null;
        while (true) {
            try {
                proxyInfo = ProxyPool.getProxyPool().getCurrentProxyInfo();
                String result = HttpUtil.getIntance().proxyGet(urlStr, "GBK", proxyInfo);
                if (result == null || !result.startsWith("成交时间")) {
                    return Collections.emptyList();
                }
                System.out.println("成功执行代理-" + proxyInfo);
                return Arrays.stream(result.split("\n")).parallel().filter(s -> !s.startsWith("alert")).skip(1).map(l -> toShareSingeDayDetail(l.split("\t"), code, name, date)).collect(Collectors.toList());
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println("失败代理-" + proxyInfo);
                if (e.getMessage().contains("456")) {
                    ProxyPool.getProxyPool().next();
                } else if (e.getMessage().contains("Read timed out")) {
                    ProxyPool.getProxyPool().next();
                }
                System.out.println("shareDayDetail error.url=" + urlStr);
                logger.error("shareDayDetail error.url=" + urlStr);
            }
        }
    }

    public static ShareDayDetail toShareSingeDayDetail(String[] shareSingeDayDetailParam, String code, String name, Date date) {
        try {
            ShareDayDetail shareSingeDayDetail = new ShareDayDetail();
            shareSingeDayDetail.setCode(code);
            shareSingeDayDetail.setName(name);
            shareSingeDayDetail.setDate(date);
            shareSingeDayDetail.setTradeTime(DateUtil.parse(shareSingeDayDetailParam[0], DateUtil.DEFAULT_TIME));
            shareSingeDayDetail.setPrice(Double.valueOf(shareSingeDayDetailParam[1]));
            shareSingeDayDetail.setPriceChange("--".equals(shareSingeDayDetailParam[2]) ? 0.0 : Double.valueOf(shareSingeDayDetailParam[2]));
            shareSingeDayDetail.setNum(Integer.valueOf(shareSingeDayDetailParam[3]));
            shareSingeDayDetail.setMoney(Double.valueOf(shareSingeDayDetailParam[4]));
            shareSingeDayDetail.setNature(shareSingeDayDetailParam[5]);
            return shareSingeDayDetail;
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("toShareSingeDayDetail error.shareSingeDayDetailParam=" + Arrays.asList(shareSingeDayDetailParam));
            logger.error("toShareSingeDayDetail error.shareSingeDayDetailParam=" + Arrays.asList(shareSingeDayDetailParam), e);
            return null;
        }
    }

    private static Share toShare(String[] shareParam) {
        Share share = new Share();
        share.setCode(shareParam[0]);
        share.setName(shareParam[1]);
        share.setIndustry(shareParam[2]);
        share.setArea(shareParam[3]);
        share.setPe(Double.valueOf(shareParam[4]));
        share.setOutstanding(Double.valueOf(shareParam[5]));
        share.setTotals(Double.valueOf(shareParam[6]));
        share.setTotalAssets(Double.valueOf(shareParam[7]));
        share.setLiquidAssets(Double.valueOf(shareParam[8]));
        share.setFixedAssets(Double.valueOf(shareParam[9]));
        share.setEsp(Double.valueOf(shareParam[12]));
        share.setBvps(Double.valueOf(shareParam[13]));
        share.setPb(Double.valueOf(shareParam[14]));
        share.setTimeToMarket(DateUtil.parse(shareParam[15]));
        share.setUndp(Double.valueOf(shareParam[16]));
        share.setHolders(Double.valueOf(shareParam[22]));
        return share;
    }

    public static List<ShareDayRight> historyRight(String url, String code, String name, String year, String jidu) {
        String result;
        int sheep = 0;
        while (true) {
            result = HttpUtil.getIntance().get(url + code + ".phtml?year=" + year + "&jidu=" + jidu);
            if (result == null) {
                try {
                    Thread.sleep(3000 * ++sheep);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = result.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
            result = result.substring(result.indexOf("<tableid=\"FundHoldSharesTable\">"), result.lastIndexOf("<divclass=\"clearit\">"));
            String singe;
            List<ShareDayRight> shareSingeDayRights = new ArrayList<>();
            while (result.contains("<atarget='_blank'")) {
                singe = result.substring(result.indexOf("<atarget='_blank'"), result.indexOf("</tr>", result.indexOf("<atarget='_blank'") + 1));
                result = result.substring(result.indexOf(singe) + singe.length() + 1);
                shareSingeDayRights.add(parserShareDayRight(singe, code, name));
            }
            return shareSingeDayRights;
        }
    }

    public static ShareDayRight parserShareDayRight(String html, String code, String name) {
        String date = html.substring(html.indexOf("'>") + "'>".length(), html.indexOf("</a>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        String open = html.substring(html.indexOf("<divalign=\"center\">") + "<divalign=\"center\">".length(), html.indexOf("</div></td>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        String high = html.substring(html.indexOf("<divalign=\"center\">") + "<divalign=\"center\">".length(), html.indexOf("</div></td>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        String close = html.substring(html.indexOf("<divalign=\"center\">") + "<divalign=\"center\">".length(), html.indexOf("</div></td>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        String low = html.substring(html.indexOf("<divalign=\"center\">") + "<divalign=\"center\">".length(), html.indexOf("</div></td>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        String volume = html.substring(html.indexOf("<divalign=\"center\">") + "<divalign=\"center\">".length(), html.indexOf("</div></td>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        String totalPrice = html.substring(html.indexOf("<divalign=\"center\">") + "<divalign=\"center\">".length(), html.indexOf("</div></td>"));
        html = html.substring(html.indexOf("</div></td>") + "</div></td>".length());
        ShareDayRight shareDayRight = new ShareDayRight();
        shareDayRight.setCode(code);
        shareDayRight.setName(name);
        shareDayRight.setDate(DateUtil.parse(date));
        shareDayRight.setOpen(Double.valueOf(open));
        shareDayRight.setHigh(Double.valueOf(high));
        shareDayRight.setClose(Double.valueOf(close));
        shareDayRight.setLow(Double.valueOf(low));
        shareDayRight.setVolume(Double.valueOf(volume));
        shareDayRight.setTotalPrice(Double.valueOf(totalPrice));
        return shareDayRight;
    }

    public static List<ShareDay> history(String url, String code, String name) {
        try {
            String result = HttpUtil.getIntance().get(url);
            Map<String, JSONArray> map = JSON.parseObject(result, Map.class);
            List<ShareDay> shareSingeDays = new ArrayList<>();
            JSONArray jsonArray = map.get("record");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray jsonArray1 = (JSONArray) jsonArray.get(i);
                ShareDay shareSingeDay = new ShareDay();
                shareSingeDay.setCode(code);
                shareSingeDay.setName(name);
                shareSingeDay.setDate(DateUtil.parse(jsonArray1.get(0).toString()));
                shareSingeDay.setOpen(Double.valueOf(jsonArray1.get(1).toString()));
                shareSingeDay.setHigh(Double.valueOf(jsonArray1.get(2).toString()));
                shareSingeDay.setClose(Double.valueOf(jsonArray1.get(3).toString()));
                shareSingeDay.setLow(Double.valueOf(jsonArray1.get(4).toString()));
                shareSingeDay.setVolume(Double.valueOf(jsonArray1.get(5).toString()));
                shareSingeDay.setPriceChange(Double.valueOf(jsonArray1.get(6).toString()));
                shareSingeDay.setP1Change(Double.valueOf(jsonArray1.get(7).toString()));
                shareSingeDay.setMa5(Double.valueOf(jsonArray1.get(8).toString()));
                shareSingeDay.setMa10(Double.valueOf(jsonArray1.get(9).toString()));
                shareSingeDay.setMa20(Double.valueOf(jsonArray1.get(10).toString()));
                shareSingeDay.setV1Ma5(Double.valueOf(jsonArray1.get(11).toString().replaceAll(",", "")));
                shareSingeDay.setV1Ma10(Double.valueOf(jsonArray1.get(12).toString().replaceAll(",", "")));
                shareSingeDay.setV1Ma20(Double.valueOf(jsonArray1.get(13).toString().replaceAll(",", "")));
                if (jsonArray1.size() >= 15) {
                    shareSingeDay.setTurnover(Double.valueOf(jsonArray1.get(14).toString()));
                }
                shareSingeDays.add(shareSingeDay);
            }
            return shareSingeDays;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("history error.url=" + url, e);
            System.out.println("history error.url=" + url);
            return Collections.emptyList();
        }
    }

}
