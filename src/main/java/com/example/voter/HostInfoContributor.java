package com.example.voter;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Component
public class HostInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        InetAddress ip = null;
        Map<String, String> hostMap = new HashMap<>();

        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        hostMap.put("ipAddress", ip.getHostAddress());
        hostMap.put("hostname", ip.getHostName());
        builder.withDetail("hostInfo", hostMap);
    }

}
