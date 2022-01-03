package OGTSystem.util;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

// 1.创建CallBack
public final class AsyncHttpRequestCallback implements FutureCallback<HttpResponse> {

        public void failed(final Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }

        public void completed(final HttpResponse response) {

            try {
                System.out.println("收到集群服务器的执行结果: " + EntityUtils.toString(response.getEntity()));

                // 一旦结果为成功则改变
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void cancelled() {
            System.out.println("cancelled");
        }
    }

