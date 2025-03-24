package com.etf.crm.entities;

import com.etf.crm.enums.CustomerSessionMode;
import com.etf.crm.enums.CustomerSessionOutcome;
import com.etf.crm.enums.CustomerSessionType;
import com.etf.crm.enums.CustomerSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Table(name = "customer_sessions")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSessionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSessionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSessionMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSessionOutcome outcome;

    @Column(nullable = false, name = "session_start", updatable = false)
    private LocalDateTime sessionStart = LocalDateTime.now();

    @Column(name = "session_end")
    private LocalDateTime sessionEnd;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

    @CreationTimestamp
    @Column(name = "date_created")
    private Instant dateCreated;

    @UpdateTimestamp
    @Column(name = "date_modified")
    private Instant dateModified;

    @Column(name = "deleted")
    private Boolean deleted = false;
}
