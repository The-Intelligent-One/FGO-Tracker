package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import lombok.Getter;

import java.util.List;

public class MaterialManagementService {
    private final FileManagementServiceFacade fileServiceFacade;
    private final DataRequestService requestService;

    @Getter
    private List<UpgradeMaterial> materials;
    @Getter
    private boolean iconsResized;

    public MaterialManagementService(FileManagementServiceFacade fileServiceFacade, DataRequestService requestService) {
        this.fileServiceFacade = fileServiceFacade;
        this.requestService = requestService;
    }

    public void loadMaterialDataFromCache(String gameRegion) {
        materials = fileServiceFacade.loadMaterialData(gameRegion);
        iconsResized = true;
    }

    public void downloadNewMaterialData(String gameRegion) {
        iconsResized = false;
        materials = requestService.getAllMaterialData(gameRegion);
        materials.forEach(material -> material.setIconImage(requestService.getImageForMaterial(material)));
    }

    public void saveMaterialData(String gameRegion) {
        fileServiceFacade.saveMaterialData(materials, gameRegion);
    }
}
