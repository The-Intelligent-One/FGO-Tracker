package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MaterialManagementService {
    @Autowired
    private FileManagementServiceFacade fileServiceFacade;
    @Autowired
    private DataRequestService requestService;

    @Getter
    private List<UpgradeMaterial> materials;
    @Getter
    private boolean iconsResized;

    public void loadMaterialDataFromCache(String gameRegion) {
        materials = fileServiceFacade.loadMaterialData(gameRegion);
        iconsResized = true;
    }

    public void downloadNewMaterialData(String gameRegion) {
        iconsResized = false;
        materials = requestService.getAllMaterialData(gameRegion);
        materials.forEach(fileServiceFacade::loadImageForMaterial);
        materials.stream()
                .filter(material -> material.getIconImage() == null)
                .forEach(material -> material.setIconImage(requestService.getImageForMaterial(material)));
    }

    public void saveMaterialData(String gameRegion) {
        fileServiceFacade.saveMaterialData(materials, gameRegion);
    }
}
