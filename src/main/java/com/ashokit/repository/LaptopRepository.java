package com.ashokit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ashokit.model.Laptop;


@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
}
