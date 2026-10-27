package student.ed.tracecode.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import student.ed.tracecode.constant.OrderStatus;
import student.ed.tracecode.entity.Book;
import student.ed.tracecode.entity.BookOrder;
import student.ed.tracecode.entity.Member;
import student.ed.tracecode.repository.BookOrderRepository;
import student.ed.tracecode.repository.BookRepository;
import student.ed.tracecode.repository.MemberRepository;

@Service
public class OrderService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookOrderRepository orderRepository;

    public OrderService(BookRepository bookRepository, MemberRepository memberRepository,
            BookOrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
    }

    // BUG: 跨「扣庫存 + 新增訂單」的操作缺少 @Transactional。
    public BookOrder placeOrder(Long memberId, Long bookId, int quantity, boolean simulateFailure) {
        // BUG: quantity == 0 也會通過。
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        Member member = memberRepository.findById(memberId).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();
        if (book.getStock() < quantity) {
            throw new IllegalStateException("not enough stock");
        }

        // BUG: 下單應扣庫存，不是增加庫存。
        book.setStock(book.getStock() + quantity);
        bookRepository.save(book);

        // BUG: 金額應為單價乘以數量。
        int amount = book.getPrice() + quantity;
        BookOrder order = orderRepository.save(new BookOrder(
                "B-" + UUID.randomUUID(), book, member, quantity, amount));

        if (simulateFailure) {
            throw new IllegalStateException("模擬付款資料建立失敗");
        }
        return order;
    }

    public List<BookOrder> findMemberHistory(Long memberId) {
        return orderRepository.findHistoryByMemberId(memberId);
    }

    public boolean isSold(Long bookId) {
        // BUG: PENDING 不代表已售出，應統計 PAID。
        return orderRepository.countByBookIdAndOrderStatus(bookId, OrderStatus.PENDING) > 0;
    }

    public long paidRevenue() {
        return orderRepository.sumAmountByStatus(OrderStatus.PAID);
    }
}
