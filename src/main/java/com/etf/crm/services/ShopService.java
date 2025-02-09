package com.etf.crm.services;

import com.etf.crm.dtos.SaveShopDto;
import com.etf.crm.dtos.ShopDto;
import com.etf.crm.entities.Region;
import com.etf.crm.entities.Shop;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.RegionRepository;
import com.etf.crm.repositories.ShopRepository;
import com.etf.crm.repositories.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;

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

    @Transactional
    public String createShop(SaveShopDto shopData) {
        User createdBy = SetCurrentUserFilter.getCurrentUser();

        User shopLeader = userRepository.findById(shopData.getShopLeader())
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));

        Region region = regionRepository.findById(shopData.getRegion())
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));

        Shop shop = Shop.builder()
                .name(shopData.getName())
                .address(shopData.getAddress())
                .shopLeader(shopLeader)
                .region(region)
                .createdBy(createdBy)
                .deleted(false)
                .build();

        shopRepository.save(shop);
        return SHOP_CREATED;
    }
}
