CREATE TABLE FXS.V1_TRANSACTIONS
(
    ID                   NUMBER(19)        NOT NULL PRIMARY KEY,
    SYS_CRE_ID           VARCHAR2(30 CHAR) NOT NULL,
    SYS_CRE_DTTM         TIMESTAMP(6)      NOT NULL,
    SYS_UPD_ID           VARCHAR2(30 CHAR),
    SYS_UPD_DTTM         TIMESTAMP(6),
    TR_REFERENCE_ID      VARCHAR2(255 CHAR),
    CHANNEL              NUMBER(3),
    BASE_ACQUIRER_ID     VARCHAR2(255 CHAR),
    BASE_ACQUIRER_NAME   VARCHAR2(255 CHAR),
    BASE_ACQUIRER_TYPE   VARCHAR2(255 CHAR),
    TARGET_ACQUIRER_ID   VARCHAR2(255 CHAR),
    TARGET_ACQUIRER_NAME VARCHAR2(255 CHAR),
    TARGET_ACQUIRER_TYPE VARCHAR2(255 CHAR),
    TYPE                 VARCHAR2(255 CHAR),
    PURPOSE              VARCHAR2(255 CHAR),
    STATUS               VARCHAR2(255 CHAR),
    CURRENCY             VARCHAR2(255 CHAR),
    AMOUNT               NUMBER(38, 2),
    BEFORE_BALANCE       NUMBER(38, 2),
    AFTER_BALANCE        NUMBER(38, 2),
    EXCHANGE_RATE        NUMBER(38, 2),
    TRANSFER_DATE        VARCHAR2(255 CHAR),
    COMPLETE_DATE        TIMESTAMP(6),
    CANCEL_DATE          TIMESTAMP(6),
    ORG_TRANSACTION_ID   NUMBER(19),
    ORG_TR_REFERENCE_ID  VARCHAR2(255 CHAR),
    REQUEST_BY           VARCHAR2(255 CHAR),
    REQUEST_NOTE         VARCHAR2(255 CHAR)
);