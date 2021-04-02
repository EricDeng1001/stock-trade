package org.example.trade.adapter.jpa;

import org.example.trade.adapter.jpa.model.account.AccountId;
import org.example.trade.adapter.jpa.model.account.asset.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface JpaAssetRepository extends JpaRepository<Asset, AccountId> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Asset> findById(AccountId accountId);
}
