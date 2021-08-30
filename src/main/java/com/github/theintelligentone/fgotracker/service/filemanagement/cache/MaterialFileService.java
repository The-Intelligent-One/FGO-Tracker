package com.github.theintelligentone.fgotracker.service.filemanagement.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;

public class MaterialFileService {
    private static final String PNG_FORMAT = "png";
    private static final String MATERIAL_DATA_FILE = "mats.json";
    private static final String IMAGE_FOLDER_PATH = "images/";

    private final FileService fileService;

    public MaterialFileService(FileService fileService) {
        this.fileService = fileService;
        fileService.createCacheStructureForDirectory(IMAGE_FOLDER_PATH);
        fileService.copyImagesFromOfflineBackupToCache(IMAGE_FOLDER_PATH);
    }

    public void saveMaterialData(List<UpgradeMaterial> materials, String gameRegion) {
        materials.forEach(mat -> fileService.saveImageToCache(mat.getIconImage(), IMAGE_FOLDER_PATH + mat.getId(), PNG_FORMAT));
        fileService.saveDataToCache(materials, gameRegion + "_" + MATERIAL_DATA_FILE);
    }

    public List<UpgradeMaterial> loadMaterialData(String gameRegion) {
        List<UpgradeMaterial> itemList = fileService.loadDataListFromCache(gameRegion + "_" + MATERIAL_DATA_FILE,
                new TypeReference<>() {});
        itemList.forEach(this::loadImageForMaterial);
        return itemList;
    }

    public void prepareOfflineMaterialData() {
        fileService.copyOfflineBackupToCache("NA_" + MATERIAL_DATA_FILE);
        fileService.copyOfflineBackupToCache("JP_" + MATERIAL_DATA_FILE);
        fileService.copyImagesFromOfflineBackupToCache(IMAGE_FOLDER_PATH);
    }

    public void loadImageForMaterial(UpgradeMaterial material) {
        material.setIconImage(fileService.getImageFromFolder(IMAGE_FOLDER_PATH, material.getId() + "." + PNG_FORMAT));
    }
}
