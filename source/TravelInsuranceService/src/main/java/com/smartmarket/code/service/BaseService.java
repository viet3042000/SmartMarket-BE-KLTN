package com.smartmarket.code.service;

import org.springframework.transaction.annotation.Transactional;

public interface BaseService<T> {
    T create(T object);
    T update(T object);
    T delete(Long id);
    T getDetail(Long id);
}
