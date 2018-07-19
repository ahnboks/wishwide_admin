package com.wishwide.wishwide.controller;


import com.wishwide.wishwide.domain.*;
import com.wishwide.wishwide.file.FileManager;
import com.wishwide.wishwide.persistence.ar.MarkerDataFileRepository;
import com.wishwide.wishwide.persistence.ar.MarkerImageRepository;
import com.wishwide.wishwide.persistence.device.DeviceImageRepository;
import com.wishwide.wishwide.persistence.device.DeviceModelImageRepository;
import com.wishwide.wishwide.persistence.product.ProductImageRepository;
import com.wishwide.wishwide.persistence.store.StoreFileRepository;
import com.wishwide.wishwide.persistence.store.StoreImageRepository;
import lombok.extern.java.Log;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

import static com.wishwide.wishwide.file.FileManager.removeCloudFile;

@Controller
@RequestMapping("/file/")
@Log
public class FileController {
    @Autowired
    StoreImageRepository storeImageRepository;

    @Autowired
    StoreFileRepository storeFileRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    DeviceImageRepository deviceImageRepository;

    @Autowired
    MarkerDataFileRepository markerDataFileRepository;

    @Autowired
    MarkerImageRepository markerImageRepository;

    @Autowired
    DeviceModelImageRepository deviceModelImageRepository;

    @GetMapping(value = "/download")
    public void downloadFile(@RequestParam("fileName") String fileName,
                             @RequestParam("dbFileName") String dbFileName,
                             HttpServletRequest request,
                             HttpServletResponse response)
            throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        String url1 = FileManager.getCdnAddress()+ dbFileName;
        log.info(url1);
        URL url = new URL(FileManager.getCdnAddress() + dbFileName);
        log.info("경로" + FileManager.getContainerName());
        BufferedInputStream bs = null;
        OutputStream out = response.getOutputStream();

        try {
            bs = new BufferedInputStream(url.openStream());

            byte[] buffer = new byte[bs.available()]; //임시로 읽는데 쓰는 공간
            int byteRead = -1;

            //브라우저별 파일명 지정
            String header = getBrowser(request);
            if (header.contains("MSIE")) {
                String docName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                response.setHeader("Content-Disposition", "attachment;filename=" + docName + ";");
            } else {
                String docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");
            }

            response.setHeader("Content-Type", setContentType(dbFileName));
            response.setHeader("Content-Transfer-Encoding", "binary;");
            response.setHeader("Pragma", "no-cache;");
            response.setHeader("Expires", "-1;");

            while ((byteRead = bs.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }

            response.setContentLength(byteRead);

            log.info("파일 다운로드 성공");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bs.close();
        }

    }

    @GetMapping("/remove/storeFile/{storeId}/{fileTypeCode}")
    public ResponseEntity removeStoreFile(@PathVariable("storeId") String storeId,
                                          @PathVariable("fileTypeCode") String fileTypeCode) {
        StoreFile storeFile = storeFileRepository.findByStoreFileAndStoreFileTypeCode(storeId, fileTypeCode);

        if(storeFile != null){
            //클라우드 파일 삭제
            removeCloudFile(storeFile.getStoreDbFileName());
            log.info("클라우드 파일삭제 완료");

            //DB 정보 삭제
            storeFileRepository.deleteById(storeFile.getStoreFileNo());
            log.info("DB 삭제 완료");
        }

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/remove/storeImage/{storeId}/{imageTypeCode}")
    public ResponseEntity removeStoreImage(@PathVariable("storeId") String storeId,
                                           @PathVariable("imageTypeCode") String imageTypeCode) {
        StoreImage storeImage = storeImageRepository.findByStoreImageAndStoreImageTypeCode(storeId, imageTypeCode);

        if(storeImage != null){
            //클라우드 파일 삭제
            removeCloudFile(storeImage.getStoreImageDbName());
            log.info("클라우드 파일삭제 완료");

            //DB 정보 삭제
            storeImageRepository.deleteById(storeImage.getStoreImageNo());
            log.info("DB 삭제 완료");
        }

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/remove/deviceImage/{deviceNo}")
    public ResponseEntity removeDeviceImage(@PathVariable("deviceNo") Long deviceNo) {
        DeviceImage deviceImage = deviceImageRepository.findByDeviceImageAndDeviceNo(deviceNo);

        if(deviceImage != null){
            //클라우드 파일 삭제
            removeCloudFile(deviceImage.getDeviceImageDbName());
            log.info("클라우드 파일삭제 완료");

            //DB 정보 삭제
            deviceImageRepository.deleteById(deviceImage.getDeviceImageNo());
            log.info("DB 삭제 완료");
        }

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/remove/deviceModelImage/{deviceModelNo}")
    public ResponseEntity removeDeviceModelImage(@PathVariable("deviceModelNo") Long deviceModelNo) {
        DeviceModelImage deviceModelImage = deviceModelImageRepository.findByDeviceModelImageAndDeviceModelNo(deviceModelNo);

        if(deviceModelImage != null){
            //클라우드 파일 삭제
            removeCloudFile(deviceModelImage.getDeviceModelImageDbName());
            log.info("클라우드 파일삭제 완료");

            //DB 정보 삭제
            deviceModelImageRepository.deleteById(deviceModelImage.getDeviceModelImageNo());
            log.info("DB 삭제 완료");
        }

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/remove/productImage/{productNo}")
    public ResponseEntity removeProductImage(@PathVariable("productNo") Long productNo) {
        ProductImage productImage = productImageRepository.findByProductNo(productNo);

        if(productImage != null){
            //클라우드 파일 삭제
            removeCloudFile(productImage.getProductImageDbName());
            log.info("클라우드 파일삭제 완료");

            //DB 정보 삭제
            deviceImageRepository.deleteById(productImage.getProductImageNo());
            log.info("DB 삭제 완료");
        }

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/remove/markerDataFile/{storeId}")
    public ResponseEntity removeMarkerDataFile(@PathVariable("storeId") String storeId) {
        log.info("storeId : "+storeId);

        MarkerDataFile markerDataFile = markerDataFileRepository.findMarkerDataFileByStoreId(storeId);

        //DAT, XML파일 삭제
        if(markerDataFile != null){
            //클라우드 DAT파일 삭제
            removeCloudFile(markerDataFile.getMarkerDatDbFile());

            //클라우드 XML파일 삭제
            removeCloudFile(markerDataFile.getMarkerXmlDbFile());

            markerDataFileRepository.deleteById(markerDataFile.getMarkerDataFileNo());

            log.info("데이터 파일 삭제 성공");

        }

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/remove/markerImageFile/{storeId}")
    public ResponseEntity removeMarkerImageFile(@PathVariable("storeId") String storeId) {

        log.info("storeId : "+storeId);

        markerImageRepository.findMarkerImageByStoreId(storeId).forEach(markerImageFile -> {
            //클라우드 파일 삭제
            if(markerImageFile != null) {
                removeCloudFile(markerImageFile.getMarkerDbFile());

                //DB 파일 삭제
                markerImageRepository.deleteById(markerImageFile.getMarkerImageNo());

                log.info("이미지파일 삭제 성공");
            }
        });

        return new ResponseEntity("success", HttpStatus.OK);
    }
//
//    @GetMapping("/remove/wide/{managerId}/{fileCategory}")
//    public ResponseEntity removeWideFile(@PathVariable("managerId") String managerId,
//                                         @PathVariable("fileCategory") String fileCategory) {
//        WideFile wideFile = wideFileRepository.findByWideFile(managerId, fileCategory);
//
//        if (wideFile != null) {
//            if (removeCloudFile(wideFile.getWideFileDbFile())) {
//                wideFileRepository.deleteById(wideFile.getWideFileNo());
//            }
//        }
//
//        return new ResponseEntity("success", HttpStatus.OK);
//    }

    //현재 브라우저 정보 가져오기
    private static String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        if (header.contains("MSIE")) {
            return "MSIE";
        } else if (header.contains("Chrome")) {
            return "Chrome";
        } else if (header.contains("Opera")) {
            return "Opera";
        }
        return "Firefox";
    }

    private static String setContentType(String dbFileName) {
        String fileExtension = dbFileName.substring(dbFileName.indexOf(".", 0) + 1);
        String contentType = "";
        switch (fileExtension) {
            case "txt":
                contentType = "text/plain";
                break;
            case "html":
            case "htm":
                contentType = "text/html";
                break;
            case "gif":
                contentType = "image/gif";
                break;
            case "jpeg":
            case "jpg":
            case "jpe":
                contentType = "image/jpeg";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "ief":
                contentType = "image/ief";
                break;
            case "tiff":
            case "tif":
                contentType = "image/tiff";
                break;
            case "asf":
                contentType = "video/x-ms-asf";
                break;
            case "avi":
                contentType = "video/avi";
                break;
            case "doc":
                contentType = "application/msword";
                break;
            case "zip":
                contentType = "application/zip";
                break;
            case "xls":
                contentType = "application/vnd.ms-excel";
                break;
            case "ppt":
                contentType = "application/vnd.ms-PowerPoint";
                break;
            case "wav":
                contentType = "audio/wav";
                break;
            case "mp3":
                contentType = "audio/mpeg3";
                break;
            case "mpg":
            case "mpeg":
                contentType = "video/mpeg";
                break;
            case "rtf":
                contentType = "application/rtf";
                break;
            case "mp4":
                contentType = "video/mp4";
                break;
            default:
                contentType = "application/octet-stream";
                break;
        }
        return contentType;
    }

}