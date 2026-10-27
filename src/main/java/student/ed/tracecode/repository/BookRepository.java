package student.ed.tracecode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import student.ed.tracecode.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    // BUG: active 與有庫存應同時成立。
    @Query("select b from Book b where b.status = 1 or b.stock > 0 order by b.id")
    List<Book> findSellableBooks();

    @Query("select b from Book b where lower(b.name) like lower(:keyword) order by b.id")
    List<Book> searchByName(@Param("keyword") String keyword);

    // BUG: page 是頁碼，不是 SQL offset。
    @Query(value = "select * from books order by id limit :size offset :page", nativeQuery = true)
    List<Book> findPage(@Param("page") int page, @Param("size") int size);
}
