package OGTSystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaiduCloudTextReview {

    @Value("${baiduyun.textReview.url}")
    String baiduyunTextReviewUrl;

    @Autowired
    OgtSystemInitializer ogtsysteminitializer;

    public boolean BaiduCloudTextReview(String text) {

        String path = baiduyunTextReviewUrl;
        ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.POST, path);
        // 设置header参数
//        request.addHeaderParameter("Content-Type", "application/json;charset=UTF-8");
        String accessToken = ogtsysteminitializer.getBaiduCloudTextReviewAccessToken();
        // 设置query参数
        request.addQueryParameter("access_token", accessToken);
        // 设置jsonBody参数
        String jsonBody = "text=" + text;
        request.setJsonBody(jsonBody);
        ApiExplorerClient client = new ApiExplorerClient();
        try {
            ApiExplorerResponse response = client.sendRequest(request);
            // 返回结果格式为Json字符串
            System.out.println(response.getResult());
            JSONObject obj = JSON.parseObject(response.getResult());
            String flag = obj.getString("conclusionType");
            String flagText = obj.getString("conclusion");
            System.out.println("Message Review flag" + flag);
            System.out.println("Message Review flag Text" + flagText);

            if ("2".equals(flag)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
