# trace_code_261027：Spring Data JPA 資料庫除錯練習

本專案模仿 `trace_code_26102001` 的 trace-code 教學方式，並以
`gtalent_spring_boot_260801` 的「會員、書籍、訂單」架構為題材。程式可以編譯，
但刻意留下 **10 個資料庫/JPA 錯誤**，請依 console 輸出逐題追蹤與修正。

> 這是題目版，不是完成版。原始碼中的 `BUG` 註解是查找入口，不代表完整答案。

## 練習範圍

```text
BookstoreDemoRunner
       │ constructor injection
       ├──────────────┐
       ▼              ▼
BookQueryService    OrderService
       │              │
       └──────┬───────┘
              ▼
Spring Data JpaRepository
              ▼
MySQL ← Hibernate 建表 ← data.sql 初始資料
```

會練習：

- `@Entity`、`@ManyToOne`、`@JoinColumn`
- constructor injection
- JPQL 與 native SQL
- `LIKE` 模糊查詢
- 分頁 offset
- entity 關聯欄位的 derived query
- dirty checking 與 `save`
- 訂單金額快照、庫存異動
- `@Transactional` 與 rollback
- `count`、`sum` 聚合查詢

## 執行方式

請先啟動 MySQL。預設設定為：

```text
database: trace_code_261027（會自動建立）
username: root
password: root
```

需要時可用環境變數覆寫：

```bash
export DB_URL='jdbc:mysql://localhost:3306/trace_code_261027?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Taipei'
export DB_USERNAME='root'
export DB_PASSWORD='your-password'
./mvnw spring-boot:run
```

每次啟動使用 `create-drop` 重建資料，適合反覆練習。Runner 會捕捉每題的 exception，
因此單一題失敗後仍可觀察後續輸出。建議一次只修一題，再重新啟動比對結果。

## 初始資料需求

### 書籍

| ID | 書名 | 正確價格 | 庫存 | 狀態 |
|---:|---|---:|---:|---:|
| 101 | Java 入門 | 450 | 5 | 1 |
| 102 | Spring Boot 實戰 | 620 | 0 | 1 |
| 103 | Spring Data JPA | 580 | 4 | 1 |
| 104 | MySQL 設計 | 500 | 3 | 1 |
| 105 | 已下架的舊書 | 300 | 8 | 0 |

### 既有訂單

| 訂單 | 買家 | 書籍 | 數量 | 金額 | 狀態 |
|---|---|---|---:|---:|---|
| B-DEMO-001 | amy (ID 2) | Java 入門 | 1 | 450 | PAID |
| B-DEMO-002 | ed (ID 1) | MySQL 設計 | 1 | 500 | CANCELLED |

## 10 題學生任務

### 1. Service 注入後仍然 NullPointerException

Spring 明明成功建立 `BookQueryService`，呼叫 repository 時卻是 `null`。比較
`OrderService` 與 `BookQueryService` 的 constructor injection 寫法。

### 2. SQL 初始資料與需求不一致

`Java 入門` 的價格應為 450。追蹤 Hibernate 建表後由哪個檔案放入初始資料，
並修正錯誤值。

### 3. 可販售書籍條件錯誤

可販售必須同時符合 `status = 1` 與 `stock > 0`，正確結果為 ID
`101、103、104`。檢查 JPQL 使用 `AND` 還是 `OR`。

### 4. 書名關鍵字查不到資料

搜尋 `Spring` 應找到 ID `102、103`。確認 SQL `LIKE` 的 `%` 應由 repository
還是 service 加入。

### 5. 第二頁資料重疊

頁碼從 0 開始；`page=1, size=2` 應得到 ID `103、104`。將頁碼換算為 SQL
需要的 offset，或改用 Spring Data 的 `Pageable`。

### 6. 會員訂單歷史查錯欄位

會員 ID 2 應只有訂單 `B-DEMO-001`。沿著 `BookOrder` 的關聯與資料表 foreign key，
找出 native SQL 篩選錯誤的 column。

### 7. 訂單金額計算錯誤

會員 1 購買 2 本 `Spring Data JPA`，訂單金額應是 `580 × 2 = 1160`，而且金額
是下單當下的價格快照。

### 8. 0 本訂單被接受

數量必須大於 0。無效輸入不得修改庫存，也不得新增訂單；驗證應在任何 repository
寫入以前完成。

### 9. 下單後庫存方向相反

ID 103 原庫存 4，購買 2 本後應為 2。確認庫存應加上還是扣除購買數量。

> 第 7～9 題會共用下單流程。修正後若要逐題觀察，請每次重啟以還原初始資料。

### 10. 發生例外時資料沒有 rollback

ID 104 下單流程在儲存訂單後模擬失敗。正確結果是庫存與訂單都維持執行前狀態，
`order count before/after` 必須相同。讓整個 use case 位於同一個 transaction。

## Bonus：狀態與聚合

- `isSold(101)` 應為 `true`：只有 `PAID` 才代表售出，`PENDING` 不算。
- 修完前 10 題並重新啟動後，初始 `paid revenue` 應為 `450`。

## 完成條件

1. 10 個區段皆不出現未預期 exception。
2. 可販售書籍只有 101、103、104。
3. 搜尋 Spring 得到 102、103。
4. 第二頁得到 103、104。
5. Amy 的歷史訂單只有 B-DEMO-001。
6. 2 本 JPA 書訂單金額為 1160，庫存由 4 變 2。
7. 數量 0 被拒絕，且不產生任何資料異動。
8. 模擬失敗後訂單數與庫存皆 rollback。
9. 已付款收入與售出狀態只採用 `PAID` 訂單。

## 專案結構

```text
src/main/java/student/ed/tracecode/
├── TraceCodeApplication.java
├── constant/OrderStatus.java
├── entity/{Book,Member,BookOrder}.java
├── repository/{Book,Member,BookOrder}Repository.java
├── service/{BookQuery,Order}Service.java
└── runner/BookstoreDemoRunner.java
```
