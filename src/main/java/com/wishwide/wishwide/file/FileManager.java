package com.wishwide.wishwide.file;

import com.ncloud.filestorage.FSRestClient;
import com.ncloud.filestorage.model.*;
import com.wishwide.wishwide.alarm.AlarmManager;
import com.wishwide.wishwide.domain.*;
import lombok.extern.java.Log;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.LazyToOne;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;

@Log
@Configuration
public class FileManager {
    @Value("${ncloud.host-name}")
    private static String hostName ;

    @Value("${ncloud.port-number}")
    private static int portNumber;

    @Value("${ncloud.access-key}")
    private static String accessKey ;

    @Value("${ncloud.secret-key}")
    private static String secretKey ;

    @Value("${ncloud.container-name}")
    private static String containerName;

    @Value("${ncloud.cdn}")
    private static String cdnAddress ;

    @Value("${thumbnail}")
    private static String thumbnailPath;
    private StoreFile storeImage;

    public static HashMap<String, String> saveCloudFile(MultipartFile multipartFile) {
        HashMap<String, String> fileInfo = new HashMap<>();
        //DB저장용 파일명(
        fileInfo.put("dbFile", RandomStringUtils.randomAlphanumeric(32));
        //확장자
        fileInfo.put("fileExtension", FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase());
        //실제파일명
        fileInfo.put("fileName", multipartFile.getOriginalFilename());
        //파일사이즈
        fileInfo.put("fileSize", String.valueOf(multipartFile.getSize()));
        FSRestClient.initialize();

        log.info(""+multipartFile.getSize());

        FSRestClient fsRestClient = new FSRestClient(hostName, portNumber, accessKey, secretKey);
        log.info("등록 시도" + fileInfo);
        try {
            /** 파일 **/
            FSResourceID fileResourceID = new FSResourceID(
                    containerName +
                            "/contents/" +
                            fileInfo.get("dbFile") + "." + fileInfo.get("fileExtension"));

            //저장할 파일 내용과 content-type을 지정한다
            FSUploadSourceInfo fileUploadSourceInfo = new FSUploadSourceInfo(
                    multipartFile.getInputStream(),
                    multipartFile.getContentType(),
                    multipartFile.getBytes().length,
                    null);

            //파일을 업로드 한다
            FSUploadFileResult fileResult = fsRestClient.uploadFile(fileResourceID, fileUploadSourceInfo);

            if (isSetcontentType(fileInfo.get("fileExtension"))) {
                /** 썸네일 **/
                File file = new File(thumbnailPath);

                if(!file.exists())
                    file.mkdirs();

                File tempThumbnailFile = createThumbnail(multipartFile, fileInfo.get("fileExtension"), fileInfo.get("dbFile"));
                InputStream thumbnailFile = new FileInputStream(tempThumbnailFile);

                FSResourceID thumbnailResourceID = new FSResourceID(
                        containerName +
                                "/contents/" +
                                "th_" + fileInfo.get("dbFile") + "." + fileInfo.get("fileExtension"));

                //저장할 파일 내용과 content-type을 지정한다
                FSUploadSourceInfo thumbnailUploadSourceInfo = new FSUploadSourceInfo(
                        thumbnailFile,
                        multipartFile.getContentType(),
                        thumbnailFile.available(),
                        null);

                //파일을 업로드 한다
                FSUploadFileResult thumbnailResult = fsRestClient.uploadFile(thumbnailResourceID, thumbnailUploadSourceInfo);

                tempThumbnailFile.delete();
                thumbnailFile.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FSClientException e) {
            // 클라이언트 단에서 발생한 exception
            e.printStackTrace();
        } catch (FSServiceException e) {
            // 서비스 단에서 발생한 exception
            e.printStackTrace();
        } finally {
            FSRestClient.destroy();
        }

        return fileInfo;
    }

    public static HashMap<String, String> saveCloudFile2(File file, String fileExtension, String originalFileName) {
        HashMap<String, String> fileInfo = new HashMap<>();
        //DB저장용 파일명(
        fileInfo.put("dbFile", file.getName().substring(0, file.getName().indexOf(".")));
        //확장자
        fileInfo.put("fileExtension", fileExtension);
        //실제파일명
        fileInfo.put("fileName", originalFileName+"."+fileExtension);
        //파일사이즈
        fileInfo.put("fileSize", String.valueOf(file.length()));
        FSRestClient.initialize();

        log.info("디비파일명"+file.getName()+"확장자"+fileExtension);

        log.info(""+file.length());

        byte[] fileBytes = new byte[(int) file.length()];

        FSRestClient fsRestClient = new FSRestClient(hostName, portNumber, accessKey, secretKey);
        log.info("등록 시도" + fileInfo);
        try {
            /** 파일 **/
            FSResourceID fileResourceID = new FSResourceID(
                    containerName +
                            "/contents/" +
                            file.getName());

            InputStream inputStream = new FileInputStream(file);

            //저장할 파일 내용과 content-type을 지정한다
            FSUploadSourceInfo fileUploadSourceInfo = new FSUploadSourceInfo(
                    inputStream,
                    "video/mp4",
                    fileBytes.length,
                    null);

            //파일을 업로드 한다
            FSUploadFileResult fileResult = fsRestClient.uploadFile(fileResourceID, fileUploadSourceInfo);

            log.info("업로드 성공");

        } catch (FSClientException e) {
            // 클라이언트 단에서 발생한 exception
            e.printStackTrace();
        } catch (FSServiceException e) {
            // 서비스 단에서 발생한 exception
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            FSRestClient.destroy();
        }

        return fileInfo;
    }

    //매장 파일 등록
    public static StoreFile saveDBStoreFile(String fileTypeCode,
                                            String storeId,
                                            HashMap<String, String> fileInfo) {
        String dbFile = fileInfo.get("dbFile"); //DB파일명
        String fileExtension = fileInfo.get("fileExtension");   //파일확장자
        String fileName = fileInfo.get("fileName"); //실제파일명
        int fileSize = Integer.parseInt(fileInfo.get("fileSize")); //파일크기

        StoreFile storeFile = new StoreFile();
        storeFile.setStoreFileName(fileName);
        storeFile.setStoreDbFileName(dbFile + "." + fileExtension);
        storeFile.setStoreFileSize(fileSize);
        storeFile.setStoreFileExtension(fileExtension);
        storeFile.setStoreFileUrl(cdnAddress + dbFile + "." + fileExtension);
        storeFile.setStoreId(storeId);
        storeFile.setStoreFileTypeCode(fileTypeCode);

        return storeFile;
    }

    //매장 이미지 파일 등록
    public static StoreImage saveDBStoreImage(String fileTypeCode,
                                            String storeId,
                                            HashMap<String, String> fileInfo) {
        String dbFile = fileInfo.get("dbFile"); //DB파일명
        String fileExtension = fileInfo.get("fileExtension");   //파일확장자
        String fileName = fileInfo.get("fileName"); //실제파일명
        int fileSize = Integer.parseInt(fileInfo.get("fileSize")); //파일크기

        StoreImage storeImage = new StoreImage();
        storeImage.setStoreImageName(fileName);
        storeImage.setStoreImageDbName(dbFile + "." + fileExtension);
        storeImage.setStoreImageSize(fileSize);
        storeImage.setStoreImageExtension(fileExtension);
        storeImage.setStoreImageUrl(cdnAddress + dbFile + "." + fileExtension);
        storeImage.setStoreImageThumbnailName("th_" + dbFile + "." + fileExtension);
        storeImage.setStoreImageThumbnailUrl(cdnAddress + "th_" + dbFile + "." + fileExtension);
        storeImage.setStoreId(storeId);
        storeImage.setStoreImageTypeCode(fileTypeCode);

        return storeImage;
    }

    //디바이스File 테이블에 저장
    public static DeviceImage saveDBDeviceImage(Long deviceNo,
                    String deviceImageTypeCode,
                                                String storeId,
                                                HashMap<String, String> fileInfo) {
        String dbFile = fileInfo.get("dbFile"); //DB파일명
        String fileExtension = fileInfo.get("fileExtension");   //파일확장자
        String fileName = fileInfo.get("fileName"); //실제파일명
        int fileSize = Integer.parseInt(fileInfo.get("fileSize")); //파일크기

        DeviceImage deviceFile = new DeviceImage();
        deviceFile.setDeviceImageName(fileName);
        deviceFile.setDeviceImageDbName(dbFile + "." + fileExtension);
        deviceFile.setDeviceImageSize(fileSize);
        deviceFile.setDeviceImageExtension(fileExtension);
        deviceFile.setDeviceImageThumbnailName("th_" + dbFile + "." + fileExtension);
        deviceFile.setDeviceImageThumbnailUrl(cdnAddress + "th_" + dbFile + "." + fileExtension);
        deviceFile.setDeviceImageUrl(cdnAddress + dbFile + "." + fileExtension);
        deviceFile.setStoreId(storeId);
        deviceFile.setDeviceImageTypeCode(deviceImageTypeCode);
        deviceFile.setDeviceNo(deviceNo);

        return deviceFile;
    }

    //디바이스모델File 테이블에 저장
    public static DeviceModelImage saveDBDeviceModelImage(Long deviceModelNo,
            String deviceImageTypeCode,
                                                HashMap<String, String> fileInfo) {
        String dbFile = fileInfo.get("dbFile"); //DB파일명
        String fileExtension = fileInfo.get("fileExtension");   //파일확장자
        String fileName = fileInfo.get("fileName"); //실제파일명
        int fileSize = Integer.parseInt(fileInfo.get("fileSize")); //파일크기

        DeviceModelImage deviceModelImage = new DeviceModelImage();
        deviceModelImage.setDeviceModelImageName(fileName);
        deviceModelImage.setDeviceModelImageDbName(dbFile + "." + fileExtension);
        deviceModelImage.setDeviceModelImageSize(fileSize);
        deviceModelImage.setDeviceModelImageExtension(fileExtension);
        deviceModelImage.setDeviceModelImageThumbnailName("th_" + dbFile + "." + fileExtension);
        deviceModelImage.setDeviceModelImageThumbnailUrl(cdnAddress + "th_" + dbFile + "." + fileExtension);
        deviceModelImage.setDeviceModelImageUrl(cdnAddress + dbFile + "." + fileExtension);
        deviceModelImage.setDeviceModelImageTypeCode(deviceImageTypeCode);
        deviceModelImage.setDeviceModelNo(deviceModelNo);

        return deviceModelImage;
    }

    //MarkerDataFile 테이블에 저장
    public static MarkerDataFile saveDBMarkerDataFile(HashMap<String, String> datFileInfo,
                                                      HashMap<String, String> xmlFileInfo) {
        String datDbFile = datFileInfo.get("dbFile"); //DB파일명
        String datFileExtension = datFileInfo.get("fileExtension");   //파일확장자
        String datFileName = datFileInfo.get("fileName"); //실제파일명
        String datFileSize = datFileInfo.get("fileSize"); //파일크기

        String xmlDbFile = xmlFileInfo.get("dbFile"); //DB파일명
        String xmlFileExtension = xmlFileInfo.get("fileExtension");   //파일확장자
        String xmlFileName = xmlFileInfo.get("fileName"); //실제파일명
        String xmlFileSize = xmlFileInfo.get("fileSize"); //파일크기

        MarkerDataFile markerDataFile = new MarkerDataFile();

        //DAT 파일 설정 셋팅
        markerDataFile.setMarkerDatDbFile(datDbFile + "." + datFileExtension);
        markerDataFile.setMarkerDatFileName(datFileName);
        markerDataFile.setMarkerDatFileExtension(datFileExtension);
        markerDataFile.setMarkerDatFileUrl(cdnAddress + datDbFile + "." + datFileExtension);
        markerDataFile.setMarkerDatFileSize(Integer.parseInt(datFileSize));

        //XML 파일 설정 셋팅
        markerDataFile.setMarkerXmlDbFile(xmlDbFile + "." + xmlFileExtension);
        markerDataFile.setMarkerXmlFileName(xmlFileName);
        markerDataFile.setMarkerXmlFileExtension(xmlFileExtension);
        markerDataFile.setMarkerXmlFileUrl(cdnAddress + xmlDbFile + "." + xmlFileExtension);
        markerDataFile.setMarkerXmlFileSize(Integer.parseInt(xmlFileSize));

        return markerDataFile;
    }

    //MarkerImage 테이블에 저장
    public static MarkerImageFile saveDBMarkerImage(Long markerNo,
                                                    String storeId,
                                                    HashMap<String, String> fileInfo) {
        String dbFile = fileInfo.get("dbFile"); //DB파일명
        String fileExtension = fileInfo.get("fileExtension");   //파일확장자
        String fileName = fileInfo.get("fileName"); //실제파일명
        String fileSize = fileInfo.get("fileSize"); //파일크기

        MarkerImageFile wwMarkerImageFile = new MarkerImageFile();

        wwMarkerImageFile.setStoreId(storeId);
        wwMarkerImageFile.setMarkerNo(markerNo);
        wwMarkerImageFile.setMarkerDbFile(dbFile + "." + fileExtension);
        wwMarkerImageFile.setMarkerImageName(fileName);
        wwMarkerImageFile.setMarkerImageExtension(fileExtension);
        wwMarkerImageFile.setMarkerImageUrl(cdnAddress + dbFile + "." + fileExtension);
        wwMarkerImageFile.setMarkerImageThumbnailName("th_" + dbFile + "." + fileExtension);
        wwMarkerImageFile.setMarkerImageThumbnailUrl(cdnAddress + "th_" + dbFile + "." + fileExtension);
        wwMarkerImageFile.setMarkerImageSize(Integer.parseInt(fileSize));

        return wwMarkerImageFile;
    }

    //Product Image 테이블에 저장
    public static ProductImage saveDBProductImage(String storeId,
                                                  HashMap<String, String> fileInfo) {
        String dbFile = fileInfo.get("dbFile"); //DB파일명
        String fileExtension = fileInfo.get("fileExtension");   //파일확장자
        String fileName = fileInfo.get("fileName"); //실제파일명
        String fileSize = fileInfo.get("fileSize"); //파일크기

        ProductImage productImage = new ProductImage();

        productImage.setStoreId(storeId);
        productImage.setProductImageName(fileName);
        productImage.setProductImageDbName(dbFile + "." + fileExtension);
        productImage.setProductImageExtension(fileExtension);
        productImage.setProductImageUrl(cdnAddress + dbFile + "." + fileExtension);
        productImage.setProductImageThumbnailName("th_" + dbFile + "." + fileExtension);
        productImage.setProductImageThumbnailUrl(cdnAddress + "th_" + dbFile + "." + fileExtension);
        productImage.setProductImageSize(Integer.parseInt(fileSize));

        return productImage;
    }


    //실제 파일 삭제
    public static boolean removeCloudFile(String filePath) {
        boolean isDelete = false;

        FSRestClient.initialize();

        FSRestClient fsRestClient = new FSRestClient(hostName, portNumber, accessKey, secretKey);

        try {
            /**썸네일**/
            FSResourceID thumbnailResourceID = new FSResourceID(
                    containerName +
                            "/contents/" +
                            "th_" +
                            filePath);

            //파일을 업로드 한다
            fsRestClient.deleteFile(thumbnailResourceID, null);

            /**파일**/
            FSResourceID fileResourceID = new FSResourceID(
                    containerName +
                            "/contents/" +
                            filePath);

            log.info("URL 정보 : "+containerName+"/contents/"+filePath);

            //파일을 업로드 한다
            int deleteState = fsRestClient.deleteFile(fileResourceID, null);

            if (deleteState == 1) {
                isDelete = true;
                log.info("삭제완료");
            }
        } catch (FSClientException e) {
            // 클라이언트 단에서 발생한 exception
            e.printStackTrace();
        } catch (FSServiceException e) {
            // 서비스 단에서 발생한 exception
            e.printStackTrace();
        } finally {
            FSRestClient.destroy();
        }

        return isDelete;
    }

    private static File createThumbnail(
            MultipartFile multipartFile,
            String fileExtension,
            String dbFile) throws IOException {
//        String thumbnailPath = thumbnailPath;
        Thumbnails.of(multipartFile.getInputStream()).size(75, 75).outputFormat(fileExtension).toFile(thumbnailPath + dbFile + "." + fileExtension);

        File thumbnailFile = new File(thumbnailPath + dbFile + "." + fileExtension);

        if (thumbnailFile.exists())
            return thumbnailFile;

        return null;
    }


    @Value("${ncloud.host-name}")
    private void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Value("${ncloud.port-number}")
    private void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    @Value("${ncloud.access-key}")
    private void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @Value("${ncloud.secret-key}")
    private void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Value("${ncloud.container-name}")
    private void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @Value("${ncloud.cdn}")
    public void setCdnAddress(String cdnAddress) {
        this.cdnAddress = cdnAddress;
    }

    @Value("${thumbnail}")
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public static String getHostName() {
        return hostName;
    }

    public static int getPortNumber() {
        return portNumber;
    }

    public static String getAccessKey() {
        return accessKey;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    public static String getContainerName() {
        return containerName;
    }

    public static String getCdnAddress() {
        return cdnAddress;
    }

    public static boolean isSetcontentType(String fileExtension) {
        boolean isThumbnail = false;
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
                isThumbnail = true;
                break;
            case "jpeg":
            case "jpg":
            case "jpe":
                contentType = "image/jpeg";
                isThumbnail = true;
                break;
            case "png":
                contentType = "image/png";
                isThumbnail = true;
                break;
            case "ief":
                contentType = "image/ief";
                isThumbnail = true;
                break;
            case "tiff":
            case "tif":
                contentType = "image/tiff";
                isThumbnail = true;
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
            default:
                contentType = "application/octet-stream";
                break;
        }
        return isThumbnail;
    }

    private static String setcontentType(String fileExtension) {
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
            default:
                contentType = "application/octet-stream";
                break;
        }
        return contentType;
    }
}