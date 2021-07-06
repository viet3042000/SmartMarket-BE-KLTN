package com.smartmarket.code.service;

public interface BaseService<T> {
    T create(T object);
    T update(T object);
    T delete(Long id);
    T getDetail(Long id);
}
