package com.xdpsx.ecommerce.services.impl;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.xdpsx.ecommerce.dtos.common.PageParams;
import com.xdpsx.ecommerce.dtos.common.PageResponse;
import com.xdpsx.ecommerce.dtos.vendor.VendorRequest;
import com.xdpsx.ecommerce.dtos.vendor.VendorResponse;
import com.xdpsx.ecommerce.entities.Vendor;
import com.xdpsx.ecommerce.exceptions.BadRequestException;
import com.xdpsx.ecommerce.exceptions.ResourceNotFoundException;
import com.xdpsx.ecommerce.mappers.VendorMapper;
import com.xdpsx.ecommerce.repositories.VendorRepository;
import com.xdpsx.ecommerce.services.UploadFileService;
import com.xdpsx.ecommerce.services.VendorService;
import com.xdpsx.ecommerce.specifications.SimpleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xdpsx.ecommerce.constants.AppConstants.*;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorMapper vendorMapper;
    private final VendorRepository vendorRepository;
    private final UploadFileService uploadFileService;

    private final SimpleSpecification<Vendor> spec;

    @Override
    public PageResponse<VendorResponse> getAllVendors(PageParams request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        Page<Vendor> vendorsPage = vendorRepository.findAll(
                spec.getSearchSpec(request.getSearch(), request.getSort()),
                pageable
        );
        List<VendorResponse> vendorResponses = vendorsPage.getContent().stream()
                .map(vendorMapper::fromEntityToResponse)
                .collect(Collectors.toList());

        return PageResponse.<VendorResponse>builder()
                .items(vendorResponses)
                .pageNum(vendorsPage.getNumber() + 1)
                .pageSize(vendorsPage.getSize())
                .totalItems(vendorsPage.getTotalElements())
                .totalPages(vendorsPage.getTotalPages())
                .build();
    }

    @Override
    public VendorResponse getVendor(Integer id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with id=[%s] not found!".formatted(id)));
        return vendorMapper.fromEntityToResponse(vendor);
    }

    @Override
    public VendorResponse createVendor(VendorRequest request, MultipartFile file) {
        Vendor vendor = vendorMapper.fromRequestToEntity(request);
        if (vendorRepository.existsByName(vendor.getName())){
            throw new BadRequestException("Vendor with name=[%s] has already existed!".formatted(vendor.getName()));
        }

        String logoUrl = uploadVendorLogo(file);
        vendor.setLogo(logoUrl);

        Vendor savedVendor = vendorRepository.save(vendor);
        return vendorMapper.fromEntityToResponse(savedVendor);
    }

    private String uploadVendorLogo(MultipartFile file){
        Map uploadOptions = ObjectUtils.asMap(
                "folder", VENDOR_IMG_FOLDER,
                "transformation", new Transformation().width(VENDOR_IMG_WIDTH).crop("scale")
        );
        Map uploadFile = uploadFileService.uploadFile(file, uploadOptions);
        return (String)uploadFile.get("url");
    }

    @Override
    public VendorResponse updateVendor(Integer id, VendorRequest request, MultipartFile file) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with id=[%s] not found!".formatted(id)));

        if (!vendor.getName().equals(request.getName())){
            if (vendorRepository.existsByName(request.getName())){
                throw new BadRequestException("Vendor with name=[%s] has already existed!".formatted(request.getName()));
            }
            vendor.setName(request.getName());
        }

        if (file != null) {
            String oldImageUrl = vendor.getLogo();
            String logoUrl = uploadVendorLogo(file);
            vendor.setLogo(logoUrl);
            uploadFileService.deleteImage(oldImageUrl);
        }

        Vendor savedVendor = vendorRepository.save(vendor);
        return vendorMapper.fromEntityToResponse(savedVendor);
    }

    @Override
    public void deleteVendor(Integer id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with id=[%s] not found!".formatted(id)));
        vendorRepository.delete(vendor);
        uploadFileService.deleteImage(vendor.getLogo());
    }
}
