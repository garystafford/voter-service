package com.voterapi.voter.utilities;

import com.mongodb.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Component
public class HostInfoContributor implements InfoContributor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoTemplate mongoTemplate;

    @Autowired
    public HostInfoContributor(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void contribute(Info.Builder builder) {
        InetAddress ip = InetAddress.getLoopbackAddress();
        Map<String, String> hostMap = new HashMap<>();

        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.info(String.valueOf(e));
        }
        hostMap.put("ipAddress", ip.getHostAddress());
        hostMap.put("hostname", ip.getHostName());
        builder.withDetail("appHostInfo", hostMap);

        hostMap = new HashMap<>();
        CommandResult commandResult = this.mongoTemplate.executeCommand("{ serverStatus: 1 }");
        hostMap.put("hostname", commandResult.getString("host"));
        builder.withDetail("mongoDbHostInfo", hostMap);
    }

}
