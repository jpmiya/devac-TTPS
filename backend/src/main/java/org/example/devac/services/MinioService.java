package org.example.devac.services;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName = "mascotas";

    // interno: app -> minio (docker network)
    private final String endpoint;

    // público: lo que ve el navegador
    private final String publicEndpoint;

    public MinioService() {
        this.endpoint = System.getenv().getOrDefault("MINIO_ENDPOINT", "http://minio:9000");
        this.publicEndpoint = System.getenv().getOrDefault("MINIO_PUBLIC_ENDPOINT", "http://localhost:9000");

        String accessKey = System.getenv().getOrDefault("MINIO_ACCESS_KEY", "minioadmin");
        String secretKey = System.getenv().getOrDefault("MINIO_SECRET_KEY", "minioadmin123");

        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        createBucketIfNotExists();
    }

    public String getFileUrl(String fileName) {
        return String.format("%s/%s/%s", publicEndpoint, bucketName, fileName);
    }
    
    private void createBucketIfNotExists() {
        try {
            boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!found) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
                
                // Hacer el bucket público para lectura
                String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {"AWS": "*"},
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(bucketName);
                
                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creando bucket en MinIO", e);
        }
    }

    public String uploadFile(MultipartFile file, Long mascotaId) {
        System.out.println("[MINIO] uploadFile() endpoint=" + endpoint + " bucket=" + bucketName);
        System.out.println("[MINIO] file=" + file.getOriginalFilename() + " size=" + file.getSize() + " type=" + file.getContentType());

        try {
            System.out.println("[MINIO] Subiendo foto para mascotaId=" + mascotaId);
            System.out.println("[MINIO] Original filename=" + file.getOriginalFilename());
            System.out.println("[MINIO] ContentType=" + file.getContentType());
            System.out.println("[MINIO] Size=" + file.getSize());

            String extension = getFileExtension(file.getOriginalFilename());
            String fileName = mascotaId + "_" + System.currentTimeMillis() + extension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            System.out.println("[MINIO] Subida OK → object=" + fileName);


            System.out.println("[MINIO] uploaded objectName=" + fileName);
            System.out.println("[MINIO] url=" + getFileUrl(fileName));

            return fileName;

        } catch (Exception e) {
            System.err.println("[MINIO] ERROR subiendo archivo");
            e.printStackTrace();
            throw new RuntimeException("Error subiendo archivo a MinIO", e);
        }
    }


    public InputStream getFile(String fileName) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo archivo de MinIO", e);
        }
    }
    

    
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando archivo de MinIO", e);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
