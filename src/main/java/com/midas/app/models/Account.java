package com.midas.app.models;

import com.midas.app.providers.external.stripe.ProviderType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "accounts")
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
  @Id
  @Column(name = "id")
  @GeneratedValue
  private UUID id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "created_at")
  @CreationTimestamp
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private OffsetDateTime updatedAt;

  @Column(name = "provider_type")
  private ProviderType providerType;

  @Column(name = "provider_id")
  private String providerId;

  // Getter and Setter methods for providerType
  public ProviderType getProviderType() {
    return providerType;
  }

  public void setProviderType(ProviderType providerType) {
    this.providerType = providerType;
  }

  // Getter and Setter methods for providerId
  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }
}
