package org.dimhat.auth.service.impl;

import org.dimhat.auth.service.DepartmentService;
import org.dimhat.auth.service.dto.DepartmentDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : zwj
 * @data : 2017/3/10
 */
@Transactional
@Service
public class DepartmentServiceImpl implements DepartmentService{
    @Override
    public Long save(DepartmentDTO dto) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public void update(DepartmentDTO dto) {

    }

    @Override
    public DepartmentDTO getRootDepartment(Long companyId) {
        return null;
    }
}
