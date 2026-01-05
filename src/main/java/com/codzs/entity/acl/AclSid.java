package com.codzs.entity.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * MongoDB Document representing ACL Security Identity (SID).
 * This entity stores security identities (users or roles) for ACL.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "acl_sid")
@CompoundIndexes({
    @CompoundIndex(name = "idx_acl_sid_unique", def = "{'sid': 1, 'principal': 1}", unique = true)
})
public class AclSid {

    @Id
    private String id;

    @NotNull(message = "Principal flag is required")
    private Boolean principal;

    @NotBlank(message = "SID is required")
    @Size(max = 100, message = "SID must not exceed 100 characters")
    private String sid;

    // Constructors
    public AclSid() {}

    public AclSid(String sid, boolean principal) {
        this.sid = sid;
        this.principal = principal;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Boolean getPrincipal() { return principal; }
    public void setPrincipal(Boolean principal) { this.principal = principal; }

    public boolean isPrincipal() { return Boolean.TRUE.equals(principal); }

    public String getSid() { return sid; }
    public void setSid(String sid) { this.sid = sid; }

    @Override
    public String toString() {
        return "AclSid{" +
                "id='" + id + '\'' +
                ", principal=" + principal +
                ", sid='" + sid + '\'' +
                '}';
    }
}