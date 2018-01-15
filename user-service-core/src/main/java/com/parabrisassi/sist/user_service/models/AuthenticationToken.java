package com.parabrisassi.sist.user_service.models;

import javax.persistence.*;

/**
 * Class representing an authentication token.
 */
@Entity
@Table(name = "authentication_tokens")
public class AuthenticationToken {

    /**
     * The token's id.
     */
    @Id
    private long id;

    /**
     * The {@link User} owning this token.
     */
    @JoinColumn(columnDefinition = "integer", name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    /**
     * Indicates if the token is valid (i.e not blacklisted)
     */
    @Column(name = "valid")
    private boolean valid;

    /* package */ AuthenticationToken() {
        // For Hibernate.
    }

    /**
     * Constructor.
     *
     * @param id   The token's id.
     * @param user The {@link User} that owns this token.
     * @throws NullPointerException If the {@code owner} is {@code null}.
     */
    public AuthenticationToken(long id, User user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("The token's owner must not be null");
        }
        this.id = id;
        this.user = user;
        this.valid = true;
    }

    /**
     * @return The token's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The {@link User} that owns this token.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return {@code true} if it is a valid token, or {@code false} otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Makes this token invalid (i.e blacklists this token).
     */
    public void blacklist() {
        this.valid = false;
    }

    // ================================
    // equals, hashcode and toString
    // ================================

    /**
     * Equals based on the {@code id}.
     *
     * @param o The object to be compared with.
     * @return {@code true} if they are the equals, or {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthenticationToken)) return false;

        AuthenticationToken other = (AuthenticationToken) o;

        return id == other.id;
    }

    /**
     * @return This token's hashcode, based on the {@code id}.
     */
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Token: [ID: " + id + ']';
    }
}
