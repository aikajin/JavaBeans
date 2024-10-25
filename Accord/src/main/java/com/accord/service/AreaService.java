package com.accord.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.Area;
import com.accord.repository.AreaRepository;

@Service
public class AreaService {
    
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    public Area addArea(String name, String guidelines, MultipartFile cover_photo, MultipartFile additional_photo) {
        Area area = new Area();
        area.setName(name);
        area.setGuidelines(guidelines);
        try {
            area.setCover_name(cover_photo.getOriginalFilename());
            area.setCover_type(cover_photo.getContentType());
            area.setCover_document(cover_photo.getBytes());
            area.setAddtional_name(additional_photo.getOriginalFilename());
            area.setAddtional_type(additional_photo.getContentType());
            area.setAdditional_document(additional_photo.getBytes());
        } catch(IOException e) {
            e.printStackTrace();
        }
        Area registeredArea = areaRepository.save(area);
        return registeredArea;
    }

    public Area saveArea(Area area) {
        return areaRepository.save(area);
    }
}
