package com.etf.crm.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Table(name = "documents")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

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