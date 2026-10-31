package student.ed.tracecode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import student.ed.tracecode.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    // BUG: active 與有庫存應同時成立。
    @Query("select b from Book b where b.status = 1 and b.stock > 0 order by b.id")
    List<Book> findSellableBooks();

    @Query("select b from Book b where lower(b.name) like lower(concat('%', :keyword, '%')) order by b.id")
    List<Book> searchByName(@Param("keyword") String keyword);

    // offset: 跳過前面幾筆(偏移量), limit: 取幾筆
    @Query(value = "select * from books order by id limit :size offset :offset", nativeQuery = true)
    List<Book> findPage(@Param("offset") int offset, @Param("size") int size);
}
