package student.ed.tracecode.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import student.ed.tracecode.constant.OrderStatus;

@Entity
@Table(name = "book_orders")
public class BookOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_member_id", nullable = false)
    private Member buyer;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "order_status", nullable = false)
    private String orderStatus = OrderStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected BookOrder() {
    }

    public BookOrder(String orderNo, Book book, Member buyer, Integer quantity, Integer amount) {
        this.orderNo = orderNo;
        this.book = book;
        this.buyer = buyer;
        this.quantity = quantity;
        this.amount = amount;
    }

    @PrePersist
    void prePersist() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public Book getBook() { return book; }
    public Member getBuyer() { return buyer; }
    public Integer getQuantity() { return quantity; }
    public Integer getAmount() { return amount; }
    public String getOrderStatus() { return orderStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
