package com.myproject.models.repositories;

import com.myproject.models.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Cart entity
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    /**
     * Find cart by user ID
     * @param userId User ID
     * @return Optional Cart
     */
    Optional<Cart> findByUserId(String userId);
    
    /**
     * Delete cart by user ID
     * @param userId User ID
     */
    void deleteByUserId(String userId);
}
