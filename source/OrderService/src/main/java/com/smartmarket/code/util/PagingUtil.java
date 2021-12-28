package com.smartmarket.code.util;

import org.springframework.util.CollectionUtils;

import java.util.List;

public class PagingUtil {
    public static List getPageLimit(List dataList, int page, int size) {
        if(CollectionUtils.isEmpty(dataList)){
            return dataList;
        }
        if(dataList.size()<size && page>1){
            return null;
        }
        int fromIndex = (page - 1) * size;
        return dataList.subList(fromIndex, Math.min(fromIndex + size, dataList.size()));
    }
}
