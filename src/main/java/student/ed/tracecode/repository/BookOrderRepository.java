package student.ed.tracecode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import student.ed.tracecode.entity.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
    // BUG: 應以 buyer_member_id 篩選，這裡誤用了 book_id。
    @Query(value = "select * from book_orders where book_id = :memberId order by id", nativeQuery = true)
    List<BookOrder> findHistoryByMemberId(@Param("memberId") Long memberId);

    long countByBookIdAndOrderStatus(Long bookId, String orderStatus);

    @Query("select coalesce(sum(o.amount), 0) from BookOrder o where o.orderStatus = :status")
    Long sumAmountByStatus(@Param("status") String status);
}
