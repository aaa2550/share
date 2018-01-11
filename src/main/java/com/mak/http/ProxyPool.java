package com.mak.http;

import com.mak.dto.ProxyInfo;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by yanghailong on 2018/1/9.
 */
public class ProxyPool {

    private static ProxyPool proxyPool = new ProxyPool();
    private Vector<ProxyInfo> proxies = new Vector<>();
    private int size;
    private Random random = new Random(System.currentTimeMillis());
    private volatile boolean flag = true;

    private ProxyPool(){
    }

    public void reSetProxies(List<ProxyInfo> proxyInfos) {
        if (flag) {
            flag = false;
            synchronized (ProxyPool.class) {
                proxies.clear();
                proxies.addAll(proxyInfos);
                size = proxyInfos.size();
                flag = true;
            }
        }
    }

    public static ProxyPool getProxyPool() {
        return proxyPool;
    }

    public ProxyInfo randomProxy() {
        return proxies.get(Math.abs(random.nextInt(size)));
    }

    public void remove(ProxyInfo proxyInfo) {
        proxies.remove(proxyInfo);

    }

}
