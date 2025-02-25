CREATE TABLE FXS.V1_ACCOUNTS
(
    ID                    NUMBER(19)        NOT NULL PRIMARY KEY,
    SYS_CRE_ID            VARCHAR2(30 CHAR) NOT NULL,
    SYS_CRE_DTTM          TIMESTAMP(6)      NOT NULL,
    SYS_UPD_ID            VARCHAR2(30 CHAR),
    SYS_UPD_DTTM          TIMESTAMP(6),
    ACQUIRER_ID           VARCHAR2(255 CHAR),
    ACQUIRER_NAME         VARCHAR2(255 CHAR),
    ACQUIRER_TYPE         VARCHAR2(255 CHAR),
    CURRENCY              VARCHAR2(255 CHAR),
    BALANCE               NUMBER(38, 2),
    MIN_REQUIRED_BALANCE  NUMBER(38, 2),
    AVERAGE_EXCHANGE_RATE NUMBER(38, 2),
    DEPOSIT_AMOUNT        NUMBER(38, 2),
    STATUS                VARCHAR2(255 CHAR) CHECK (STATUS IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);

CREATE SEQUENCE FXS.V1_ACCOUNTS_SEQ MINVALUE 1 MAXVALUE 99999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 50 NOCYCLE;