package com.xdpsx.ecommerce.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.xdpsx.ecommerce.services.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadFileCloudinaryService implements UploadFileService {
    private final Cloudinary cloudinary;

    @Override
    public Map uploadFile(MultipartFile file, Map uploadOptions) {
        try{
            Map data = this.cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            return data;
        }catch (IOException io){
            throw new RuntimeException("Uploading image is failed!");
        }
    }

    @Override
    public void deleteImage(String imgURL) {
        try{
            cloudinary.uploader().destroy(getPublicIdFromUrl(imgURL), ObjectUtils.emptyMap());
        }catch (IOException io){
//            throw new RuntimeException("Deleting image is failed!");
            System.out.println("Deleting image is failed!");
        }
    }

    private String getPublicIdFromUrl(String imageUrl) {
        // Tách URL thành các phần bằng cách sử dụng split
        String[] parts = imageUrl.split("/");

        String folderName = parts[parts.length - 2];
        // Lấy phần cuối cùng của URL (tức là tên file ảnh)
        String fileName = parts[parts.length - 1];

        // Tách tên file ảnh để lấy public ID
        String[] fileNameParts = fileName.split("\\.");

        // Public ID sẽ là phần trước dấu chấm trong tên file ảnh
        String publicId = folderName + "/" +fileNameParts[0];

        return publicId;
    }
}
