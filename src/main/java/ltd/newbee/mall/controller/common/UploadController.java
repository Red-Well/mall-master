/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.common;

import jakarta.servlet.http.HttpServletRequest;
import ltd.newbee.mall.config.ProjectConfig;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 13
 * @qq交流群 791509631
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@Controller
@RequestMapping("/admin")
public class UploadController {

    private final Logger log = LoggerFactory.getLogger(UploadController.class);


    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    @PostMapping({"/upload/file"})
    @ResponseBody
    public Result upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws URISyntaxException, IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null) {
            return ResultGenerator.genFailResult("请上传图片类型的文件");
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        String newFileName = sdf.format(new Date()) + r.nextInt(100) + suffixName;
        File fileDirectory = new File(ProjectConfig.getFileUploadPath());
        // 创建文件
        File destFile = new File(ProjectConfig.getFileUploadPath() + newFileName);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            Result resultSuccess = ResultGenerator.genSuccessResult();
            resultSuccess.setData("/upload/" + newFileName);
            return resultSuccess;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
        }
    }

    @PostMapping({"/upload/files"})
    @ResponseBody
    public Result uploadV2(HttpServletRequest httpServletRequest) throws IOException {
        List<MultipartFile> multipartFiles = new ArrayList<>(8);
        if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) httpServletRequest;
            Iterator<String> iter = multiRequest.getFileNames();
            int total = 0;
            while (iter.hasNext()) {
                total += 1;
                MultipartFile file = multiRequest.getFile(iter.next());
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                if (bufferedImage != null) {
                    multipartFiles.add(file);
                }
            }
        }
        if (CollectionUtils.isEmpty(multipartFiles)) {
            return ResultGenerator.genFailResult("请选择图片类型的文件上传");
        }
        if (multipartFiles.size() > 5) {
            return ResultGenerator.genFailResult("最多上传5张图片");
        }
        List<String> fileNames = new ArrayList<>(multipartFiles.size());
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 生成文件名称通用方法
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Random r = new Random();
            String newFileName = sdf.format(new Date()) + r.nextInt(100) + suffixName;
            File fileDirectory = new File(ProjectConfig.getFileUploadPath());
            // 创建文件
            File destFile = new File(ProjectConfig.getFileUploadPath() + newFileName);
            try {
                if (!fileDirectory.exists()) {
                    if (!fileDirectory.mkdir()) {
                        throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                    }
                }
                multipartFile.transferTo(destFile);
                fileNames.add("/upload/" + newFileName);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return ResultGenerator.genFailResult("文件上传失败");
            }
        }
        Result resultSuccess = ResultGenerator.genSuccessResult();
        resultSuccess.setData(fileNames);
        return resultSuccess;
    }

}
