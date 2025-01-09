package com.food.service;

import com.food.dto.PurchaseDTO;

import java.util.List;

public interface PurchaseService {
    List<PurchaseDTO> getPurchasesByToken(String userToken);
    void savePurchase(PurchaseDTO purchaseDTO, String userToken);
}
