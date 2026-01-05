package com.codzs.entity.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * MongoDB Document representing ACL Class.
 * This entity stores object class information for ACL.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "acl_class")
public class AclClass {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Class name is required")
    @Size(max = 100, message = "Class name must not exceed 100 characters")
    private String className;

    // Constructors
    public AclClass() {}

    public AclClass(String className) {
        this.className = className;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    @Override
    public String toString() {
        return "AclClass{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}