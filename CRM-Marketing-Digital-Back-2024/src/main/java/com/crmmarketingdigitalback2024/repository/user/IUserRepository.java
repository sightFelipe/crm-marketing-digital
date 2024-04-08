package com.crmmarketingdigitalback2024.repository.user;

import com.crmmarketingdigitalback2024.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Integer> {

}
