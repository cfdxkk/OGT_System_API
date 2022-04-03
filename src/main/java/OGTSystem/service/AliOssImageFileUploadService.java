package OGTSystem.service;

import OGTSystem.interFuck.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class AliOssImageFileUploadService implements OssService {

    @Value("${aliyun.oss.maxSize}")
    private int maxSize;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.dir.prefix}")
    private String dirPrefix;

    @Autowired
    private OSS ossClient;

    @Override
    public String uploadFileAvatar(InputStream inputStream, String originalFilename) {

        String fileName = UUID.randomUUID().toString();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // oss中的文件夹名
        String objectName = dirPrefix + fileName + fileExtension;

        // 创建上传文件的元信息，可以通过文件元信息设置HTTP header(设置了才能通过返回的链接直接访问)。
        //如果不设置，直接访问url会自行下载图片，看各位自己选择
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpg");

        ossClient.putObject(bucketName, objectName, inputStream, objectMetadata);

        String url = "http://" + bucketName + "." + endpoint + "/" + objectName;
        System.out.println("fileUrl: " + url);
        return url;

    }
}
