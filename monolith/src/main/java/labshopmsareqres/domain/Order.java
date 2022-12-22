package labshopmsareqres.domain;

import labshopmsareqres.domain.OrderPlaced;
import labshopmsareqres.MonolithApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Order_table")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    private String customerId;

    private Double amount;

    @PostPersist
    public void onPostPersist() {

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        labshopmsareqres.external.DecreaseStockCommand decreaseStockCommand = new labshopmsareqres.external.DecreaseStockCommand();
        decreaseStockCommand.setQty(getQty());
        
        // mappings goes here
        MonolithApplication.applicationContext.getBean(labshopmsareqres.external.InventoryService.class)
                .decreaseStock(Long.valueOf(getProductId()), decreaseStockCommand);

        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();

    }

    @PrePersist
    public void onPrePersist() {
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = MonolithApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }

}
