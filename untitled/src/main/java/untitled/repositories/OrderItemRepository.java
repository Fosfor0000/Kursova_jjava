package untitled.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import untitled.entities.OrderItemEntity;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    // Знайти всі позиції конкретного замовлення
    List<OrderItemEntity> findByOrder_OrderId(Long orderId);
}