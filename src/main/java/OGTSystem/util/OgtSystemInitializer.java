package OGTSystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

@Component
public class OgtSystemInitializer implements ApplicationRunner {

    public static String LOCAL_HOST_TRUE_IP_ADDRESS;
    public static String BAIDU_CLOUD_ACCESS_TOKEN;

    @Value("${baiduyun.tokenUrl}")
    String baiduyunTokenUrl;

    @Value("${baiduyun.textReview.apiKey}")
    String baiduyunClientId;

    @Value("${baiduyun.textReview.secretKey}")
    String baiduyunClientSecret;

    @Override
    public void run(ApplicationArguments args){
        this.getIPAddress();
        this.getBaiduCloudTextReviewAccessToken();

    }

    private void getIPAddress() {

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
                // 腾讯云ECS获取IP的命令
                process = Runtime.getRuntime().exec("curl http://metadata.tencentyun.com/meta-data/public-ipv4");
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = input.readLine();
                input.close();
                System.out.println("通过命令行获取到真实IP: " + line);
                OgtSystemInitializer.LOCAL_HOST_TRUE_IP_ADDRESS = line;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("通过代码获取到真实IP: " + localhostIpAddress);
            OgtSystemInitializer.LOCAL_HOST_TRUE_IP_ADDRESS = localhostIpAddress;
        }
    }
    public String getBaiduCloudTextReviewAccessToken() {

        HttpURLConnection connection = null;
        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;

        try {


            URL url = new URL(baiduyunTokenUrl + "?grant_type=client_credentials&client_id=" + baiduyunClientId + "&client_secret=" + baiduyunClientSecret);
            //得到连接对象
            connection = (HttpURLConnection) url.openConnection();
            //设置请求类型
            connection.setRequestMethod("GET");
            //设置请求需要返回的数据类型和字符集类型
            connection.setRequestProperty("Content-Type", "application/json;charset=GBK");
            //允许写出
            connection.setDoOutput(true);
            //允许读入
            connection.setDoInput(true);
            //不使用缓存
            connection.setUseCaches(false);
            //得到响应码
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                //得到响应流
                InputStream inputStream = connection.getInputStream();
                //将响应流转换成字符串
                resultBuffer = new StringBuffer();
                String line;
                buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                String baiduYunAccessTokenRequestResult = resultBuffer.toString();

                JSONObject obj = JSON.parseObject(baiduYunAccessTokenRequestResult);

                String baiduYunAccessToken = obj.getString("access_token");

                System.out.println("token is: " + baiduYunAccessToken);
                OgtSystemInitializer.BAIDU_CLOUD_ACCESS_TOKEN = baiduYunAccessToken;

                return baiduYunAccessToken;


            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
