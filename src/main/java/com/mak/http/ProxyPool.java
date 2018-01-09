package com.mak.http;

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
    private static Vector<Proxy> proxies = new Vector<>();
    private static int size;
    private static Random random = new Random(System.currentTimeMillis());

    private ProxyPool(){
    }

    public void setProxies(List<String> ips) {
        ips.forEach(i->proxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(i,80))));
        size = ips.size();
    }

    public static ProxyPool getProxyPool() {
        return proxyPool;
    }

    public static Proxy randomProxy() {
        return proxies.get(Math.abs(random.nextInt(size)));
    }

}
