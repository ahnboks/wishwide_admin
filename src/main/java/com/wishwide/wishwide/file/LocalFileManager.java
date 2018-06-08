package com.wishwide.wishwide.file;

import lombok.extern.java.Log;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;

@Log
public class LocalFileManager {


    //로컬 저장소에 저장
    public static HashMap<String, String> saveCloudFile(MultipartFile multipartFile) {
        HashMap<String, String> fileInfo = new HashMap<>();
        //DB저장용 파일명(
        fileInfo.put("dbFile", RandomStringUtils.randomAlphanumeric(32));
        //확장자
        fileInfo.put("fileExtension", FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase());
        //실제파일명
        fileInfo.put("fileName", multipartFile.getOriginalFilename());


        OutputStream out = null;
        try {
            out = new FileOutputStream(
                    "D://"   //저장경로
                            + fileInfo.get("dbFile")    //DB파일명
                            + "."
                            + fileInfo.get("fileExtension"));   //파일확장자

            BufferedInputStream bis = new BufferedInputStream(multipartFile.getInputStream());
            byte[] buffer = new byte[8106];
            int read;
            while ((read = bis.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }

        } catch (IOException ioe) {
            log.info(ioe.toString());
        } finally {
            IOUtils.closeQuietly(out);
        }

        return fileInfo;
    }

    public static void readFile(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        BufferedInputStream bufis = null;
        int data = 0;

        if (file.exists() && file.canRead()) {
            try {
                // open file.
                fis = new FileInputStream(file);
                bufis = new BufferedInputStream(fis);

                // read data from bufis's buffer.
                while ((data = bufis.read()) != -1) {
                    // TODO : use data
                    log.info("data : " + data);
                }
            } catch (IOException ioe) {
                log.info(ioe.toString());
            } finally {
                IOUtils.closeQuietly(bufis);
                IOUtils.closeQuietly(fis);
            }
        }
    }

    //실제 파일 삭제
    public static boolean removeFile(String filePath) {
        boolean isDelete = false;

        File file = new File(filePath);

        if (file.exists()) {
            isDelete = file.delete();
        } else {
            isDelete = false;
        }

        return isDelete;
    }
}
