package com.etf.crm.services;

import com.etf.crm.entities.Opportunity;
import com.etf.crm.entities.Shop;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.etf.crm.common.CrmConstants.ErrorCodes.SHOP_NOT_FOUND;
import static com.etf.crm.common.CrmConstants.ErrorCodes.USER_NOT_FOUND;

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

    public void deleteShop(Long id) {
        Shop shop = this.shopRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(SHOP_NOT_FOUND));
        shop.setDeleted(true);
        this.shopRepository.save(shop);
    }
}
