package student.ed.tracecode.runner;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import student.ed.tracecode.entity.Book;
import student.ed.tracecode.entity.BookOrder;
import student.ed.tracecode.repository.BookOrderRepository;
import student.ed.tracecode.repository.BookRepository;
import student.ed.tracecode.service.BookQueryService;
import student.ed.tracecode.service.OrderService;

@Component
public class BookstoreDemoRunner implements CommandLineRunner {
    private final BookQueryService bookQueryService;
    private final OrderService orderService;
    private final BookRepository bookRepository;
    private final BookOrderRepository orderRepository;

    public BookstoreDemoRunner(BookQueryService bookQueryService, OrderService orderService,
            BookRepository bookRepository, BookOrderRepository orderRepository) {
        this.bookQueryService = bookQueryService;
        this.orderService = orderService;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        // java lambda 多行寫法
        check("01 constructor injection", () -> {
            List<Book> books = bookQueryService.findSellableBooks();
            printBooks(books);
        });

        // java lambda 單行寫法
        // check("01 constructor injection", () -> printBooks(bookQueryService.findSellableBooks()));

        // // java lambda 初始來源: 匿名類別只有一個需要實作的method
        // Runnable r1 = new Runnable(){
        //     @Override 
        //     public void run() {
        //         List<Book> books = bookQueryService.findSellableBooks();
        //         printBooks(books);
        //     }
        // };
        // check("01 constructor injection", r1);


        check("02 seed data: Java 入門 price", () ->
                System.out.println("price = " + bookRepository.findById(101L).orElseThrow().getPrice()));
        check("03 active + stock query", () -> printBooks(bookRepository.findSellableBooks()));
        check("04 keyword contains search", () -> printBooks(bookQueryService.search("Spring")));
        check("05 page 2, size 2", () -> printBooks(bookQueryService.findPage(2, 2)));
        check("06 member 2 order history", () -> printOrders(orderService.findMemberHistory(2L)));
        check("07 amount snapshot", () -> {
            BookOrder order = orderService.placeOrder(1L, 103L, 2, false);
            System.out.println("amount = " + order.getAmount());
        });
        check("08 reject zero quantity", () -> orderService.placeOrder(1L, 103L, 0, false));
        check("09 decrease stock", () ->
                System.out.println("book 103 stock = " + bookRepository.findById(103L).orElseThrow().getStock()));
        check("10 rollback all writes", () -> {
            long before = orderRepository.count();
            try {
                orderService.placeOrder(1L, 104L, 1, true);
            } catch (RuntimeException ignored) {
                
            }
            System.out.println("order count before/after = " + before + "/" + orderRepository.count());
        });
        check("bonus sold status + paid revenue", () -> {
            System.out.println("book 101 sold = " + orderService.isSold(101L));
            System.out.println("paid revenue = " + orderService.paidRevenue());
        });

    }

    private void check(String title, Runnable action) {
        System.out.println("\n===== " + title + " =====");
        try { action.run(); } catch (Exception exception) {
            System.out.println(exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }
    }

    private void printBooks(List<Book> books) {
        books.forEach(book -> System.out.printf("%d | %-18s | price=%d | stock=%d | status=%d%n",
                book.getId(), book.getName(), book.getPrice(), book.getStock(), book.getStatus()));
        System.out.println("count = " + books.size());
    }

    private void printOrders(List<BookOrder> orders) {
        orders.forEach(order -> System.out.printf("%s | buyer=%s | book=%s | qty=%d | amount=%d | %s%n",
                order.getOrderNo(), order.getBuyer().getAccount(), order.getBook().getName(),
                order.getQuantity(), order.getAmount(), order.getOrderStatus()));
        System.out.println("count = " + orders.size());
    }
}
