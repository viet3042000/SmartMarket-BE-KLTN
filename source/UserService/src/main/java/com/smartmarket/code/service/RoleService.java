package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.request.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;


public interface RoleService {

    public ResponseEntity<?> createRole(@Valid @RequestBody BaseDetail<CreateRoleRequest> createRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    public ResponseEntity<?> updateRole(@Valid @RequestBody BaseDetail<UpdateRoleRequest> updateRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    public ResponseEntity<?> deleteRole(@Valid @RequestBody BaseDetail<DeleteRoleRequest> deleteRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    public ResponseEntity<?> getListRole(@Valid @RequestBody BaseDetail<QueryRoleRequest> getListRoleRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;
}
