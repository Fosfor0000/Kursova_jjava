package untitled.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import untitled.entities.OrderEntity;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // Знайти всі замовлення конкретного клієнта
    List<OrderEntity> findByCustomer_CustomerId(Long customerId);

    // Знайти замовлення за статусом
    List<OrderEntity> findByStatus(String status);
}
