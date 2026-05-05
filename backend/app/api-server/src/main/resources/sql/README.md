# SQL Dummy Data Guide

이 디렉터리는 로컬 개발용 더미 데이터를 도메인별로 관리합니다.

## 실행 순서

아래 순서대로 실행하면 FK 제약 조건에 맞게 데이터를 초기화하고 다시 적재할 수 있습니다.

1. [00_reset.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/00_reset.sql)
2. [role_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/member/role_data.sql)
3. [member_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/member/member_data.sql)
4. [member_role_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/member/member_role_data.sql)
5. [member_address_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/member/member_address_data.sql)
6. [product_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/product/product_data.sql)
7. [cart_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/cart/cart_data.sql)
8. [cart_item_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/cart/cart_item_data.sql)
9. [order_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/order/order_data.sql)
10. [order_item_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/order/order_item_data.sql)
11. [payment_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/order/payment_data.sql)
12. [delivery_data.sql](/Users/youngminkim/projects/workspace-develop/sandbox-ecommerce-api/backend/app/api-server/src/main/resources/sql/order/delivery_data.sql)

## 데이터 기준

- `TB_ROLE`: 2건
- `TB_MEMBER`: 10건
- `TB_MEMBER_ROLE`: 10건
- `TB_MEMBER_ADDRESS`: 10건
- `TB_PRODUCT`: 30건
- `TB_CART`: 10건
- `TB_CART_ITEM`: 10건
- `TB_ORDER`: 10건
- `TB_ORDER_ITEM`: 20건
- `TB_PAYMENT`: 10건
- `TB_DELIVERY`: 10건
