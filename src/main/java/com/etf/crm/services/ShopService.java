package com.etf.crm.services;

import com.etf.crm.dtos.ShopDto;
import com.etf.crm.entities.Shop;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public ShopDto getShopById(Long id) {
        return this.shopRepository.findShopDtoByUsernameAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(SHOP_NOT_FOUND));
    }

    public List<Shop> getAllShops() {
        return this.shopRepository.findAllByDeletedFalse();
    }

    @Transactional
    public String deleteShop(Long id) {
        Shop shop = this.shopRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(SHOP_NOT_FOUND));
        shop.setDeleted(true);
        this.shopRepository.save(shop);
        return SHOP_DELETED;
    }
}
