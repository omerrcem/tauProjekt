package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.ForgotPasswordToken;
import org.springframework.data.repository.CrudRepository;

public interface ForgotPasswordTokenRepository extends CrudRepository<ForgotPasswordToken, String> {
}
