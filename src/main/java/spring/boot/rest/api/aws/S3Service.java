package spring.boot.rest.api.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import spring.boot.rest.api.exception.FileException;

import java.io.IOException;
import java.net.URL;

import static spring.boot.rest.api.util.Constants.FAILED_TO_RAED_ALL_BYTES_WHEN_GET_OBJECT_TO_S3_SERVICE;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3;

    @Autowired
    public S3Service(S3Client s3Client) {
        this.s3 = s3Client;
    }

    public String putObject(String bucketName, String key, byte[] file) {
        log.info("IN putObject() -> processing...");
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.putObject(objectRequest, RequestBody.fromBytes(file));

        URL objectUrl = getObjectUrl(bucketName, key);

        log.info("IN putObject() -> put the Object -> SUCCESSFULLY");
        return objectUrl.toString();
    }

    public byte[] getObject(String bucketName, String key){
        log.info("IN getObject() -> processing...");
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> response = s3.getObject(getObjectRequest);

        try {
            byte[] readAllBytes = response.readAllBytes();
            log.info("IN getObject() -> got the object -> SUCCESSFULLY");
            return readAllBytes;
        } catch (IOException e) {
            log.info("IN getObject() -> get the object -> FAILED");
            throw new FileException(FAILED_TO_RAED_ALL_BYTES_WHEN_GET_OBJECT_TO_S3_SERVICE, e);
        }
    }

    public String copyObject(String sourceBucket, String sourceKey, String targetBucket, String targetKey) {
        log.info("IN copyObject() -> processing...");
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(targetBucket)
                .destinationKey(targetKey)
                .build();
        s3.copyObject(copyObjectRequest);

        URL objectUrl = getObjectUrl(sourceBucket, targetKey);

        log.info("IN copyObject() -> copied the object -> SUCCESSFULLY");
        return objectUrl.toString();
    }

    public void deleteObject(String bucketName, String key) {
        log.info("IN deleteObject() -> processing...");
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.deleteObject(deleteObjectRequest);
        log.info("IN deleteObject() -> deleted the object -> SUCCESSFULLY");
    }

    private URL getObjectUrl(String bucketName, String key) {
        log.info("IN getObjectUrl() -> processing...");
        URL url = s3.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        log.info("IN getObjectUrl() -> got the object url -> SUCCESSFULLY");
        return url;
    }
}
