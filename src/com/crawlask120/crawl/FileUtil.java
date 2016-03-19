package com.crawlask120.crawl;
import java.io.File;

public class FileUtil {
	public static boolean ifExist(File fileName){
		return fileName.exists();
	}
	public static void main(String[] args) {
		String filePath = "temp\\www.120ask.com_list_gaoxueya_all_3.html";
		System.out.println(ifExist(new File(filePath)));
	}

}
