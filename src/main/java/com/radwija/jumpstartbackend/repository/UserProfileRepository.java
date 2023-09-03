package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository  extends JpaRepository<UserProfile, Long> {
}
