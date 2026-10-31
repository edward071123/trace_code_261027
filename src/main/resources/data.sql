insert into members (id, account, name, status) values (1, 'ed', 'Edward', 1);
insert into members (id, account, name, status) values (2, 'amy', 'Amy', 1);
insert into members (id, account, name, status) values (3, 'disabled', '停權會員', 0);

-- BUG: Java 入門正確價格應為 450。
insert into books (id, name, price, stock, status) values (101, 'Java 入門', 450, 5, 1);
insert into books (id, name, price, stock, status) values (102, 'Spring Boot 實戰', 620, 0, 1);
insert into books (id, name, price, stock, status) values (103, 'Spring Data JPA', 580, 4, 1);
insert into books (id, name, price, stock, status) values (104, 'MySQL 設計', 500, 3, 1);
insert into books (id, name, price, stock, status) values (105, '已下架的舊書', 300, 8, 0);

insert into book_orders (id, order_no, book_id, buyer_member_id, quantity, amount, order_status, created_at)
values (1, 'B-DEMO-001', 101, 2, 1, 450, 'PAID', '2026-10-25 10:00:00');
insert into book_orders (id, order_no, book_id, buyer_member_id, quantity, amount, order_status, created_at)
values (2, 'B-DEMO-002', 104, 1, 1, 500, 'CANCELLED', '2026-10-26 11:00:00');
