package com.etf.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveShopDto {
    private String name;
    private String address;
    private Long shopLeader;
    private Long region;
}
