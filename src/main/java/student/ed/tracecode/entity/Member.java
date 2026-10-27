package student.ed.tracecode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Byte status;

    protected Member() {
    }

    public Long getId() { return id; }
    public String getAccount() { return account; }
    public String getName() { return name; }
    public Byte getStatus() { return status; }
}
