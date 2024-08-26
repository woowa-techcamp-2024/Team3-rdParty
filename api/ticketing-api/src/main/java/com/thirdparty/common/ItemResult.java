package com.thirdparty.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResult<T> {

    private List<T> items;

    public static <T> ItemResult<T> of(List<T> items) {
        return new ItemResult<>(items);
    }
}
