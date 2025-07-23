package com.javadapur.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {

    // Gunakan folder di luar resources agar tidak tercampur dengan file lainnya
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public String uploadPhoto(MultipartFile photoFile, String uploadFolder) {
        try {
            // Periksa apakah file kosong
            if (photoFile.isEmpty()) {
                throw new RuntimeException("Tidak ada file yang dipilih untuk diupload.");
            }

            // Validasi tipe file untuk memastikan hanya gambar yang diterima
            String contentType = photoFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Hanya gambar yang diizinkan untuk diupload.");
            }

            // Validasi ukuran file (contohnya 10MB)
            long maxSize = 10 * 1024 * 1024;  // 10MB
            if (photoFile.getSize() > maxSize) {
                throw new RuntimeException("Ukuran file terlalu besar. Maksimum adalah 10MB.");
            }

            // Tentukan lokasi penyimpanan file
            Path uploadPath = Paths.get(UPLOAD_DIR + uploadFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // Membuat folder jika belum ada
            }

            // Membuat nama file unik dengan menggunakan waktu saat ini
            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Transfer file ke folder yang sudah ditentukan
            photoFile.transferTo(filePath.toFile());

            // Kembalikan path relatif yang akan disimpan di database
            return "uploads/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal menyimpan file, coba lagi.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Terjadi kesalahan saat mengupload file: " + e.getMessage());
        }
    }
}
