package com.chyld.entities;

import com.chyld.enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "exercises")
@Data
public class Exercise {
    private int id;
    private int version;
    private Type type;
    private int quantity;
    private int calories;
    private int duration;
    private User user;
    private Date created;
    private Date modified;

    @Id
    @GeneratedValue
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    @Version
    public int getVersion() {return version;}
    public void setVersion(int version) {this.version = version;}

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('SWIM', 'BIKE', 'RUN', 'LIFT')")
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    @Column
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Column
    public int getCalories() {
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Column
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name="user_id")
    @JsonIgnore
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    @CreationTimestamp
    public Date getCreated() {return created;}
    public void setCreated(Date created) {this.created = created;}

    @UpdateTimestamp
    public Date getModified() {return modified;}
    public void setModified(Date modified) {this.modified = modified;}

    @PreUpdate
    protected void onUpdate() {
        this.modified = new Date();
    }
}
