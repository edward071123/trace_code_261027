package student.ed.tracecode.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import student.ed.tracecode.entity.Book;
import student.ed.tracecode.repository.BookRepository;

@Service
@Transactional(readOnly = true)
public class BookQueryService {
    private BookRepository bookRepository;

    // BUG: constructor injection 收到 repository，卻沒有指定給欄位。
    public BookQueryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findSellableBooks() {
        return bookRepository.findSellableBooks();
    }

    public List<Book> search(String keyword) {
        // BUG: LIKE 查詢需要自行補上萬用字元。
        return bookRepository.searchByName(keyword);
    }

    // offset: 跳過前面幾筆(偏移量)
    public List<Book> findPage(int page, int size) {
        int offset = (page - 1) * size;
        //       0  =  (1 - 1) * 2;
        //       2  =  (2 - 1) * 2;
        //       4  =  (3 - 1) * 2;
        return bookRepository.findPage(offset, size);
    }
}
