package br.hallel.relational.api.app.global.service.google;

import br.hallel.relational.api.app.global.exception.DeleteImageBucketException;
import br.hallel.relational.api.app.global.exception.EditImageBucketException;
import br.hallel.relational.api.app.global.exception.SendImageBucketException;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class GoogleBucketService {

    private final Storage storage;
    private final String bucketName;

    public GoogleBucketService(Storage storage,
                               @Value("${spring.cloud.gcp.storage.bucket}")
                               String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public String sendFileToBucket(MultipartFile file, String fileName) {
        try {

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).setContentType(file.getContentType()).build();
            Blob blob = storage.create(blobInfo, file.getBytes());
            return blob.getMediaLink();
        } catch (IOException e) {
            throw new SendImageBucketException("Error saving file in bucket");
        }
    }

    public String getImageToBucket(String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            throw new FileNotFoundException("Arquivo não encontrado no Google Bucket.");
        }
        return blob.getMediaLink();
    }

    public String updateFileOfBucket(MultipartFile file, String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        try {
            Blob blob = storage.create(blobInfo, file.getBytes());
            return blob.getMediaLink();
        } catch (StorageException | IOException e) {
            throw new EditImageBucketException("Error saving file in bucket");
        }
    }

    public Boolean deleteFileOfBucket(String fileName) {
        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            return storage.delete(blobId);
        } catch (StorageException e) {
            throw new DeleteImageBucketException("Erro removing file in bucket");
        }
    }
}
