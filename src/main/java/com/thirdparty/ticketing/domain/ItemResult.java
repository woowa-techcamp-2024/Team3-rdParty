package com.thirdparty.ticketing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemResult<T> {

    private List<T> item;

    public static <T> ItemResult<T> of(List<T> items) {
        return new ItemResult<T>(items);
    }
}