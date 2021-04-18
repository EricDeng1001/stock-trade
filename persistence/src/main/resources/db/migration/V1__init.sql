CREATE TABLE `account`
(
    `account_broker_id` varchar(30) NOT NULL,
    `broker`            varchar(20) NOT NULL,
    `activated`         bit(1)      NOT NULL,
    `config`            varchar(255) DEFAULT NULL,
    PRIMARY KEY (`account_broker_id`, `broker`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `asset`
(
    `account_broker_id` varchar(30) NOT NULL,
    `broker`            varchar(20) NOT NULL,
    `usable_cash`       decimal(19, 2) DEFAULT NULL,
    PRIMARY KEY (`account_broker_id`, `broker`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `asset_cash_resources`
(
    `asset_account_broker_id` varchar(30)  NOT NULL,
    `asset_broker`            varchar(20)  NOT NULL,
    `money`                   decimal(19, 2) DEFAULT NULL,
    `stock_code`              varchar(10)    DEFAULT NULL,
    `account_broker_id`       varchar(255) NOT NULL,
    `broker`                  varchar(255) NOT NULL,
    `order_id`                int          NOT NULL,
    `trade_day`               date         NOT NULL,
    PRIMARY KEY (`asset_account_broker_id`, `asset_broker`, `account_broker_id`, `broker`, `order_id`, `trade_day`),
    CONSTRAINT `FKmd3wf3ri5x5vo49ieuw63nhqi` FOREIGN KEY (`asset_account_broker_id`, `asset_broker`) REFERENCES `asset` (`account_broker_id`, `broker`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `asset_position_resources`
(
    `asset_account_broker_id` varchar(30)  NOT NULL,
    `asset_broker`            varchar(20)  NOT NULL,
    `shares`                  decimal(19, 2) DEFAULT NULL,
    `stock_code`              varchar(255)   DEFAULT NULL,
    `account_broker_id`       varchar(255) NOT NULL,
    `broker`                  varchar(255) NOT NULL,
    `order_id`                int          NOT NULL,
    `trade_day`               date         NOT NULL,
    PRIMARY KEY (`asset_account_broker_id`, `asset_broker`, `account_broker_id`, `broker`, `order_id`, `trade_day`),
    CONSTRAINT `FKj444sjl95n50c1haufbqn2byp` FOREIGN KEY (`asset_account_broker_id`, `asset_broker`) REFERENCES `asset` (`account_broker_id`, `broker`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `asset_usable_positions`
(
    `asset_account_broker_id` varchar(30)  NOT NULL,
    `asset_broker`            varchar(20)  NOT NULL,
    `usable_positions`        decimal(19, 2) DEFAULT NULL,
    `usable_positions_key`    varchar(255) NOT NULL,
    PRIMARY KEY (`asset_account_broker_id`, `asset_broker`, `usable_positions_key`),
    CONSTRAINT `FKknkjm9o6abfk4spvafp2vc87f` FOREIGN KEY (`asset_account_broker_id`, `asset_broker`) REFERENCES `asset` (`account_broker_id`, `broker`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `orders`
(
    `account_broker_id` varchar(30) NOT NULL,
    `broker`            varchar(20) NOT NULL,
    `order_id`          int         NOT NULL,
    `trade_day`         date        NOT NULL,
    `broker_id`         varchar(255)   DEFAULT NULL,
    `closed_at`         datetime(6)    DEFAULT NULL,
    `created_at`        datetime(6)    DEFAULT NULL,
    `price`             decimal(19, 2) DEFAULT NULL,
    `shares`            decimal(19, 2) DEFAULT NULL,
    `status`            int            DEFAULT NULL,
    `stock_code`        varchar(255)   DEFAULT NULL,
    `submitted_at`      datetime(6)    DEFAULT NULL,
    `version`           bigint      NOT NULL,
    PRIMARY KEY (`account_broker_id`, `broker`, `order_id`, `trade_day`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `order_trades`
(
    `order_account_broker_id` varchar(30) NOT NULL,
    `order_broker`            varchar(20) NOT NULL,
    `order_order_id`          int         NOT NULL,
    `order_trade_day`         date        NOT NULL,
    `dealt_on`                datetime(6)    DEFAULT NULL,
    `price`                   decimal(19, 2) DEFAULT NULL,
    `shares`                  decimal(19, 2) DEFAULT NULL,
    `trade_broker_id`         varchar(255)   DEFAULT NULL,
    KEY `FK9t1j3ld6y7yqij57r6ol3xove` (`order_account_broker_id`, `order_broker`, `order_order_id`, `order_trade_day`),
    CONSTRAINT `FK9t1j3ld6y7yqij57r6ol3xove` FOREIGN KEY (`order_account_broker_id`, `order_broker`, `order_order_id`,
                                                          `order_trade_day`) REFERENCES `orders` (`account_broker_id`, `broker`, `order_id`, `trade_day`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;