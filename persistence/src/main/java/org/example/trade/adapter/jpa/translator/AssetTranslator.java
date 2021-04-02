package org.example.trade.adapter.jpa.translator;

import org.example.finance.domain.Money;
import org.example.trade.adapter.jpa.model.account.asset.Asset;
import org.example.trade.adapter.jpa.model.account.asset.CashResource;
import org.example.trade.adapter.jpa.model.account.asset.PositionResource;
import org.example.trade.adapter.jpa.model.account.asset.Resource;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssetTranslator {

    public static org.example.trade.domain.account.asset.Asset from(Asset record) {
        return new org.example.trade.domain.account.asset.Asset(
            AccountTranslator.from(record.getAccountId()),
            record.getUsablePositions().entrySet().stream().collect(
                Collectors.toMap(
                    e -> SecurityCode.valueOf(e.getKey()),
                    e -> Shares.valueOf(e.getValue())
                )),
            Stream.concat(record.getCashResources().entrySet().stream(),
                          record.getPositionResources().entrySet().stream()).collect(
                Collectors.toMap(
                    e -> OrderTranslator.from(e.getKey()),
                    e -> from(e.getValue())
                )),
            Money.valueOf(record.getUsableCash())
        );
    }

    public static org.example.trade.domain.account.asset.Resource<?> from(Resource record) {
        SecurityCode securityCode = SecurityCode.valueOf(record.getStockCode());
        if (record.getClass() == CashResource.class) {
            return new org.example.trade.domain.account.asset.CashResource(
                securityCode,
                Money.valueOf(((CashResource) record).getMoney())
            );
        } else if (record.getClass() == PositionResource.class) {
            return new org.example.trade.domain.account.asset.PositionResource(
                securityCode,
                Shares.valueOf(((PositionResource) record).getShares())
            );
        }
        throw new IllegalStateException();
    }

    public static Asset from(org.example.trade.domain.account.asset.Asset asset) {
        Asset record = new Asset();
        record.setAccountId(AccountTranslator.from(asset.id()));
        record.setUsableCash(asset.usableCash().value());
        record.setUsablePositions(
            asset.usablePositions().entrySet().stream().collect(
                Collectors.toMap(
                    e -> e.getKey().value(),
                    e -> e.getValue().value()
                ))
        );
        record.setCashResources(
            asset.resources().entrySet().stream()
                .filter(e -> e.getValue().getClass() == org.example.trade.domain.account.asset.CashResource.class)
                .collect(
                    Collectors.toMap(
                        e -> OrderTranslator.from(e.getKey()),
                        e -> {
                            org.example.trade.domain.account.asset.CashResource value =
                                (org.example.trade.domain.account.asset.CashResource) e.getValue();
                            CashResource cashResource = new CashResource();
                            cashResource.setStockCode(value.securityCode().value());
                            cashResource.setMoney(value.remain().value());
                            return cashResource;

                        }
                    )));
        record.setPositionResources(
            asset.resources().entrySet().stream()
                .filter(e -> e.getValue().getClass() == org.example.trade.domain.account.asset.PositionResource.class)
                .collect(
                    Collectors.toMap(
                        e -> OrderTranslator.from(e.getKey()),
                        e -> {
                            org.example.trade.domain.account.asset.PositionResource value =
                                (org.example.trade.domain.account.asset.PositionResource) e.getValue();
                            PositionResource positionResource = new PositionResource();
                            positionResource.setStockCode(value.securityCode().value());
                            positionResource.setShares(value.remain().value());
                            return positionResource;
                        }
                    )));

        return record;
    }

}
