package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ServiceConfigRepository;
import com.smartmarket.code.service.ServiceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ServiceConfigServiceImpl implements ServiceConfigService {

    @Autowired
    ServiceConfigRepository serviceConfigRepository ;

    @Override
    public HashMap<String, Object> getAllCacheListServiceConfig(){
        HashMap<String, Object> map=new HashMap<String, Object>();

        List<Object[]> listResult =  serviceConfigRepository.findAllServiceConfigToMap()  ;

        for (Object[] result : listResult) {
            if (result[1] != null ){
                map.put(result[0].toString(), result[1].toString());
            }else {
                map.put(result[0].toString(), "");
            }
        }

        return map;
    }

}
