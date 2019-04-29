package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

/**
 * 文件上传controller
 * @author 李帅辉
 *
 */
@RestController
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL; 
	
	@RequestMapping("upload")
	public Result upload(MultipartFile file) {
		
		String originalFilename=file.getOriginalFilename();//获取文件名
		String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);//得到文件扩展名
		
		try {
			//创建一个FastDFS的客户端
			util.FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
			String fileId = client.uploadFile(file.getBytes(),extName);
			//拼接返回的url和IP地址，装拼成完整的url
			String url = FILE_SERVER_URL + fileId;//图片的完整地址
			return new Result(true, url);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
		
	}
	
}
