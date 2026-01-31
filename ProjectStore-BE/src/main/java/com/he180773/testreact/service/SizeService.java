package com.he180773.testreact.service;


import com.he180773.testreact.entity.Size;
import com.he180773.testreact.repository.SizeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    public List<Size> getSizesByIds(List<Long> ids) {
        return sizeRepository.findAllById(ids);
    }
}