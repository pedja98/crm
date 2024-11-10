package com.etf.crm.services;

import com.etf.crm.entities.Opportunity;
import com.etf.crm.entities.Shop;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.SHOP_NOT_FOUND;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public Shop getShopById(Long id) {
        return this.shopRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(SHOP_NOT_FOUND));
    }

    public List<Shop> getAllShops() {
        return this.shopRepository.findAllByDeletedFalse();
    }
}
