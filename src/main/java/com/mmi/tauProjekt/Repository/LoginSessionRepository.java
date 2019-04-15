package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.LoginSession;
import org.springframework.data.repository.CrudRepository;

public interface LoginSessionRepository extends CrudRepository<LoginSession,String> {
}
