-- Fictional ephemeral demo; initialize only a NEW instance.
CREATE DATABASE `lyd_mall_demo` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `lyd_mall_demo`;
SET NAMES utf8mb4;
CREATE TABLE `oms_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NULL,
  `order_sn` VARCHAR(512) NULL,
  `coupon_id` BIGINT NULL,
  `create_time` DATETIME NULL,
  `member_username` VARCHAR(512) NULL,
  `total_amount` DECIMAL(19,4) NULL,
  `pay_amount` DECIMAL(19,4) NULL,
  `freight_amount` DECIMAL(19,4) NULL,
  `promotion_amount` DECIMAL(19,4) NULL,
  `integration_amount` DECIMAL(19,4) NULL,
  `coupon_amount` DECIMAL(19,4) NULL,
  `discount_amount` DECIMAL(19,4) NULL,
  `pay_type` INT NULL,
  `source_type` INT NULL,
  `status` INT NULL,
  `delivery_company` VARCHAR(512) NULL,
  `delivery_sn` VARCHAR(512) NULL,
  `auto_confirm_day` INT NULL,
  `integration` INT NULL,
  `growth` INT NULL,
  `bill_type` INT NULL,
  `bill_header` VARCHAR(512) NULL,
  `bill_content` VARCHAR(512) NULL,
  `bill_receiver_phone` VARCHAR(512) NULL,
  `bill_receiver_email` VARCHAR(512) NULL,
  `receiver_name` VARCHAR(512) NULL,
  `receiver_phone` VARCHAR(512) NULL,
  `receiver_post_code` VARCHAR(512) NULL,
  `receiver_province` VARCHAR(512) NULL,
  `receiver_city` VARCHAR(512) NULL,
  `receiver_region` VARCHAR(512) NULL,
  `receiver_detail_address` VARCHAR(512) NULL,
  `note` TEXT NULL,
  `confirm_status` INT NULL,
  `delete_status` INT NULL,
  `use_integration` INT NULL,
  `payment_time` DATETIME NULL,
  `delivery_time` DATETIME NULL,
  `receive_time` DATETIME NULL,
  `comment_time` DATETIME NULL,
  `modify_time` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_order_sn` (`order_sn`),
  KEY `idx_order_member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `oms_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NULL,
  `order_sn` VARCHAR(512) NULL,
  `spu_id` BIGINT NULL,
  `spu_name` VARCHAR(512) NULL,
  `spu_pic` VARCHAR(512) NULL,
  `spu_brand` VARCHAR(512) NULL,
  `category_id` BIGINT NULL,
  `sku_id` BIGINT NULL,
  `sku_name` VARCHAR(512) NULL,
  `sku_pic` VARCHAR(512) NULL,
  `sku_price` DECIMAL(19,4) NULL,
  `sku_quantity` INT NULL,
  `sku_attrs_vals` VARCHAR(512) NULL,
  `promotion_amount` DECIMAL(19,4) NULL,
  `coupon_amount` DECIMAL(19,4) NULL,
  `integration_amount` DECIMAL(19,4) NULL,
  `real_amount` DECIMAL(19,4) NULL,
  `gift_integration` INT NULL,
  `gift_growth` INT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_item_order` (`order_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pms_attr` (
  `attr_id` BIGINT NOT NULL AUTO_INCREMENT,
  `attr_name` VARCHAR(512) NULL,
  `search_type` INT NULL,
  `icon` VARCHAR(512) NULL,
  `value_select` TEXT NULL,
  `attr_type` INT NULL,
  `enable` BIGINT NULL,
  `catelog_id` BIGINT NULL,
  `show_desc` INT NULL,
  PRIMARY KEY (`attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pms_attr_attrgroup_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `attr_id` BIGINT NULL,
  `attr_group_id` BIGINT NULL,
  `attr_sort` INT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pms_attr_group` (
  `attr_group_id` BIGINT NOT NULL AUTO_INCREMENT,
  `attr_group_name` VARCHAR(512) NULL,
  `sort` INT NULL,
  `descript` TEXT NULL,
  `icon` VARCHAR(512) NULL,
  `catelog_id` BIGINT NULL,
  PRIMARY KEY (`attr_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pms_product_attr_value` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `spu_id` BIGINT NULL,
  `attr_id` BIGINT NULL,
  `attr_name` VARCHAR(512) NULL,
  `attr_value` TEXT NULL,
  `attr_sort` INT NULL,
  `quick_show` INT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_spu_attr` (`spu_id`, `attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pms_sku_info` (
  `sku_id` BIGINT NOT NULL AUTO_INCREMENT,
  `spu_id` BIGINT NULL,
  `sku_name` VARCHAR(512) NULL,
  `sku_desc` TEXT NULL,
  `catalog_id` BIGINT NULL,
  `brand_id` BIGINT NULL,
  `sku_default_img` VARCHAR(512) NULL,
  `sku_title` VARCHAR(512) NULL,
  `sku_subtitle` VARCHAR(512) NULL,
  `price` DECIMAL(19,4) NULL,
  `sale_count` BIGINT NULL,
  PRIMARY KEY (`sku_id`),
  KEY `idx_sku_spu` (`spu_id`),
  KEY `idx_sku_catalog` (`catalog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pms_sku_sale_attr_value` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sku_id` BIGINT NULL,
  `attr_id` BIGINT NULL,
  `attr_name` VARCHAR(512) NULL,
  `attr_value` TEXT NULL,
  `attr_sort` INT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sale_attr_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `wms_ware_sku` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sku_id` BIGINT NULL,
  `ware_id` BIGINT NULL,
  `stock` INT NULL,
  `sku_name` VARCHAR(512) NULL,
  `stock_locked` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_sku_ware` (`sku_id`, `ware_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE UNIQUE INDEX demo_order_sn ON oms_order(order_sn);
CREATE INDEX demo_owner_orders ON oms_order(member_id,note(16),id);
CREATE INDEX demo_order_expiry ON oms_order(status,create_time);
CREATE INDEX demo_order_items ON oms_order_item(order_sn,sku_id);
CREATE UNIQUE INDEX demo_warehouse_sku ON wms_ware_sku(sku_id,ware_id);
