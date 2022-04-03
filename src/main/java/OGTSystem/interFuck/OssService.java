package OGTSystem.interFuck;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface OssService {
    String uploadFileAvatar(InputStream inputStream, String originalFilename);
}
