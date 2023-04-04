package com.cursoudemy.backendcursoudemy.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cursoudemy.backendcursoudemy.entities.ExposureEntity;

@Repository
public interface ExposureRepository extends CrudRepository<ExposureEntity, Long>  {
    ExposureEntity findById(long id);
}
