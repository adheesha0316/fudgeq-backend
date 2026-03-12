package com.fudgeq.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class Profile extends BaseEntity{

    @Id
    @Column(nullable = false, updatable = false)
    private String profileId;

    private String phoneNumber;
    private String address;
    private String profileImgPath;
    private String bio;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
}
