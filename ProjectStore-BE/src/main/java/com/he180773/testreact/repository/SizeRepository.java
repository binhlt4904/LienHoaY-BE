package com.he180773.testreact.repository;


import com.he180773.testreact.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    List<Size> findAllById(Long id);
}
