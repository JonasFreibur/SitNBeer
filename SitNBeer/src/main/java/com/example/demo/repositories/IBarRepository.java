package com.example.demo.repositories;

import java.util.Optional;

import com.example.demo.models.Bar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBarRepository extends JpaRepository<Bar, Long>{
    Bar findByName(String name);
    Optional<Bar> findById(Integer id);
}