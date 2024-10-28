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

    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }


}
