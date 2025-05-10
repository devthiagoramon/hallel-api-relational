package br.hallel.relational.api.app.global.service.google;

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

    public String sendImageToBucket(MultipartFile file, String fileName) throws
            IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).setContentType(file.getContentType()).build();
        Blob blob = storage.create(blobInfo, file.getBytes());
        return blob.getMediaLink();
    }

    public String getImageToBucket(String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            throw new FileNotFoundException("Arquivo não encontrado no Google Bucket.");
        }
        return blob.getMediaLink();
    }

    public String updateImageOfBucket(MultipartFile file, String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);


        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try {

            Blob blob = storage.create(blobInfo, file.getBytes());
            return blob.getMediaLink();
        } catch (StorageException e) {

            throw new IOException("Erro ao atualizar o arquivo no Google Cloud Storage", e);
        } catch (Exception e) {

            throw new IOException("Erro inesperado ao tentar atualizar o arquivo", e);
        }
    }

    public Boolean deleteImageOfBucket(String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        return storage.delete(blobId);
    }
}
