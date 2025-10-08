package com.codzs.entity.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * MongoDB Document representing ACL Entry.
 * This entity stores access control entries for ACL.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "acl_entry")
@CompoundIndexes({
    @CompoundIndex(name = "idx_acl_entry_unique", def = "{'aclObjectIdentity': 1, 'aceOrder': 1}", unique = true)
})
public class AclEntry {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "ACL object identity is required")
    private String aclObjectIdentity; // Reference to AclObjectIdentity ID

    @NotNull(message = "ACE order is required")
    private Integer aceOrder;

    @NotBlank(message = "SID is required")
    private String sid; // Reference to AclSid ID

    @NotNull(message = "Mask is required")
    private Integer mask;

    @NotNull(message = "Granting flag is required")
    private Boolean granting;

    @NotNull(message = "Audit success flag is required")
    private Boolean auditSuccess;

    @NotNull(message = "Audit failure flag is required")
    private Boolean auditFailure;

    // Constructors
    public AclEntry() {}

    public AclEntry(String aclObjectIdentity, Integer aceOrder, String sid, Integer mask, boolean granting) {
        this.aclObjectIdentity = aclObjectIdentity;
        this.aceOrder = aceOrder;
        this.sid = sid;
        this.mask = mask;
        this.granting = granting;
        this.auditSuccess = false;
        this.auditFailure = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAclObjectIdentity() { return aclObjectIdentity; }
    public void setAclObjectIdentity(String aclObjectIdentity) { this.aclObjectIdentity = aclObjectIdentity; }

    public Integer getAceOrder() { return aceOrder; }
    public void setAceOrder(Integer aceOrder) { this.aceOrder = aceOrder; }

    public String getSid() { return sid; }
    public void setSid(String sid) { this.sid = sid; }

    public Integer getMask() { return mask; }
    public void setMask(Integer mask) { this.mask = mask; }

    public Boolean getGranting() { return granting; }
    public void setGranting(Boolean granting) { this.granting = granting; }

    public boolean isGranting() { return Boolean.TRUE.equals(granting); }

    public Boolean getAuditSuccess() { return auditSuccess; }
    public void setAuditSuccess(Boolean auditSuccess) { this.auditSuccess = auditSuccess; }

    public boolean isAuditSuccess() { return Boolean.TRUE.equals(auditSuccess); }

    public Boolean getAuditFailure() { return auditFailure; }
    public void setAuditFailure(Boolean auditFailure) { this.auditFailure = auditFailure; }

    public boolean isAuditFailure() { return Boolean.TRUE.equals(auditFailure); }

    @Override
    public String toString() {
        return "AclEntry{" +
                "id='" + id + '\'' +
                ", aclObjectIdentity='" + aclObjectIdentity + '\'' +
                ", aceOrder=" + aceOrder +
                ", sid='" + sid + '\'' +
                ", mask=" + mask +
                ", granting=" + granting +
                '}';
    }
}