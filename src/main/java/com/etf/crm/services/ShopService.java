package com.etf.crm.services;

import com.etf.crm.dtos.SaveShopDto;
import com.etf.crm.dtos.ShopDto;
import com.etf.crm.entities.Region;
import com.etf.crm.entities.Shop;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.PropertyCopyException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.RegionRepository;
import com.etf.crm.repositories.ShopRepository;
import com.etf.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

    public List<ShopDto> getFilteredAndSortedShops(
            String sortBy,
            String sortOrder,
            List<Long> regions,
            List<Long> shopLeaders,
            String name) {

        List<ShopDto> shops = this.shopRepository.findAllShopDtoByDeletedFalse()
                .orElseThrow(() -> new ItemNotFoundException(SHOP_NOT_FOUND));

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("shopLeaderId", shopLeaders);
        filters.put("regionId", regions);

        List<Predicate<ShopDto>> predicates = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    return (Predicate<ShopDto>) user -> {
                        try {
                            Field field = ShopDto.class.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(user);

                            if (value instanceof String stringValue) {
                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                            } else if (value instanceof List<?> listValue) {
                                return fieldValue != null && listValue.contains(fieldValue);
                            }
                            return false;
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(ILLEGAL_SORT_PARAMETER + ": " + fieldName, e);
                        }
                    };
                })
                .toList();

        for (Predicate<ShopDto> predicate : predicates) {
            shops = shops.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<ShopDto> comparator = Comparator.comparing(shop -> {
                try {
                    Field field = ShopDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(shop);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException(ILLEGAL_SORT_PARAMETER + ": " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            shops = shops.stream().sorted(comparator).toList();
        }

        return shops;
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

    @Transactional
    public String updateShop(Long id, SaveShopDto shopDetails) {
        Shop shop = this.shopRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(SHOP_NOT_FOUND));

        for (Field field : SaveShopDto.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object newValue = field.get(shopDetails);
                if (field.getName().equals("shopLeader")) {
                    newValue = userRepository.findById((Long) field.get(shopDetails))
                            .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
                } else if (field.getName().equals("region")) {
                    newValue = regionRepository.findById((Long) field.get(shopDetails))
                            .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
                }
                Field shopField = Shop.class.getDeclaredField(field.getName());
                shopField.setAccessible(true);
                shopField.set(shop, newValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new PropertyCopyException(ENTITY_UPDATE_ERROR);
            }
        }

        return SHOP_UPDATED;
    }
}
