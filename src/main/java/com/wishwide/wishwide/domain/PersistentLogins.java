package com.wishwide.wishwide.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "persistent_logins")
@Getter
@Setter
@EqualsAndHashCode(of = "")
public class PersistentLogins {
    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Id
    @Column(name = "series", length = 64)
    private String series;

    @Column(name = "token", length = 64, nullable = false)
    private String token;

    @CreationTimestamp
    @Column(name = "last_used", length = 64, nullable = false)
    private LocalDateTime lastUsed;
}