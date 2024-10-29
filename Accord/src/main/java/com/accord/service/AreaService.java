package com.accord.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.Area;
import com.accord.repository.AreaRepository;

@Service
public class AreaService {
    
    @Autowired
    private AreaRepository areaRepository;

    public Area createArea(Area area, MultipartFile cover, MultipartFile add) {
        try {
            area.setCoverDocument(cover.getBytes());
            area.setCoverName(cover.getOriginalFilename());
            area.setCoverType(cover.getContentType());
            area.setAddDocument(add.getBytes());
            area.setAddName(add.getOriginalFilename());
            area.setAddType(add.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return areaRepository.save(area);
    }
    public Area createArea2(Area area, MultipartFile cover) {
        try {
            area.setCoverDocument(cover.getBytes());
            area.setCoverName(cover.getOriginalFilename());
            area.setCoverType(cover.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return areaRepository.save(area);
    }
    public void deleteArea(Long id) {
        areaRepository.deleteById(id);
    }
    public List<Area> getAllAreas() {
        return areaRepository.findAll(Sort.by("id").ascending());
    }

    public Area getAreaById(Long id) {
        return areaRepository.findById(id).orElse(null);
    }
}
