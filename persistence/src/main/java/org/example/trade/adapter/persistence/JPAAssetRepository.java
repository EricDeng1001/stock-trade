package org.example.trade.adapter.persistence;

import org.example.trade.adapter.persistence.model.account.AccountId;
import org.example.trade.adapter.persistence.model.account.asset.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAAssetRepository extends JpaRepository<Asset, AccountId> {
}
