package org.example.trade.adapter.persistence;

import org.example.trade.adapter.persistence.model.account.AccountId;
import org.example.trade.adapter.persistence.model.account.asset.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface JPAAssetRepository extends JpaRepository<Asset, AccountId> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Asset> findById(AccountId accountId);
}
