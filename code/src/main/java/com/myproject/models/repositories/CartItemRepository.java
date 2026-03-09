package com.myproject.models.repositories;

import com.myproject.models.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for CartItem entity
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    /**
     * Find cart item by cart ID and product ID
     * @param cartId Cart ID
     * @param productId Product ID
     * @return Optional CartItem
     */
    Optional<CartItem> findByCart_IdAndProductId(String cartId, String productId);
}
