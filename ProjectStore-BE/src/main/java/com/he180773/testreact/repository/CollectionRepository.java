package com.he180773.testreact.repository;

import com.he180773.testreact.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Collection findCollectionByName(String name);
}
