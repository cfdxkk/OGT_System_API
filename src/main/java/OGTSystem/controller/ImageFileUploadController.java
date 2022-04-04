package OGTSystem.controller;

import OGTSystem.interFuck.OssService;
import com.aliyuncs.ecs.model.v20140526.AttachKeyPairResponse;
import com.aliyuncs.ecs.model.v20140526.DetachKeyPairResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageFileUploadController {

    @Autowired
    private OssService ossService;

    //上传图片的方法
    @CrossOrigin
    @PostMapping("upload")
    @ResponseStatus(HttpStatus.OK)
    public List<String> uploadOssFile(
            @RequestParam("file") MultipartFile file
//            @RequestParam("userId") String userId,
//            @RequestParam("token") String token
    ) {
        //获取上传文件 MultipartFile
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("userId [" + userId + "]  -  token [" + token + "]");

        List<String> result = new ArrayList<>();

        result.add(ossService.uploadFileAvatar(inputStream, file.getOriginalFilename()));

        return result;
    }

}
