package com.codzs.entity.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * MongoDB Document representing ACL Object Identity.
 * This entity stores object identity information for ACL.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "acl_object_identity")
@CompoundIndexes({
    @CompoundIndex(name = "idx_acl_object_identity_unique", def = "{'objectIdClass': 1, 'objectIdIdentity': 1}", unique = true)
})
public class AclObjectIdentity {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Object ID class is required")
    private String objectIdClass; // Reference to AclClass ID

    @NotBlank(message = "Object ID identity is required")
    @Size(max = 36, message = "Object ID identity must not exceed 36 characters")
    private String objectIdIdentity;

    private String parentObject; // Reference to parent AclObjectIdentity ID

    private String ownerSid; // Reference to AclSid ID

    @NotNull(message = "Entries inheriting flag is required")
    private Boolean entriesInheriting;

    // Constructors
    public AclObjectIdentity() {}

    public AclObjectIdentity(String objectIdClass, String objectIdIdentity, boolean entriesInheriting) {
        this.objectIdClass = objectIdClass;
        this.objectIdIdentity = objectIdIdentity;
        this.entriesInheriting = entriesInheriting;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getObjectIdClass() { return objectIdClass; }
    public void setObjectIdClass(String objectIdClass) { this.objectIdClass = objectIdClass; }

    public String getObjectIdIdentity() { return objectIdIdentity; }
    public void setObjectIdIdentity(String objectIdIdentity) { this.objectIdIdentity = objectIdIdentity; }

    public String getParentObject() { return parentObject; }
    public void setParentObject(String parentObject) { this.parentObject = parentObject; }

    public String getOwnerSid() { return ownerSid; }
    public void setOwnerSid(String ownerSid) { this.ownerSid = ownerSid; }

    public Boolean getEntriesInheriting() { return entriesInheriting; }
    public void setEntriesInheriting(Boolean entriesInheriting) { this.entriesInheriting = entriesInheriting; }

    public boolean isEntriesInheriting() { return Boolean.TRUE.equals(entriesInheriting); }

    @Override
    public String toString() {
        return "AclObjectIdentity{" +
                "id='" + id + '\'' +
                ", objectIdClass='" + objectIdClass + '\'' +
                ", objectIdIdentity='" + objectIdIdentity + '\'' +
                ", entriesInheriting=" + entriesInheriting +
                '}';
    }
}