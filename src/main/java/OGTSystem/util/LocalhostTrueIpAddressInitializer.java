package OGTSystem.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

@Component
public class LocalhostTrueIpAddressInitializer implements ApplicationRunner {

    public static String LOCAL_HOST_TRUE_IP_ADDRESS;

    @Override
    public void run(ApplicationArguments args){

        // 获取当前服务器的IP地址
        String localhostIpAddress = "";
        try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            localhostIpAddress = inetaddress.getHostAddress();

        } catch (Exception e){
            System.out.println(e);
        }
        if ("127.0.0.1".equals(localhostIpAddress)){
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("curl http://metadata.tencentyun.com/meta-data/public-ipv4");
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = input.readLine();
                input.close();
                System.out.println("通过命令行获取到真实IP: " + line);
                LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS = line;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("通过代码获取到真实IP: " + localhostIpAddress);
            LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS = localhostIpAddress;
        }



    }
}
