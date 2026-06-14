CREATE DATABASE IF NOT EXISTS banco
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE banco;

CREATE TABLE IF NOT EXISTS cliente (
    cpf VARCHAR(20) NOT NULL,
    nome VARCHAR(80) NOT NULL,
    sobrenome VARCHAR(80) NOT NULL,
    rg VARCHAR(20) NOT NULL,
    endereco VARCHAR(150) NOT NULL,
    PRIMARY KEY (cpf),
    UNIQUE KEY uk_cliente_rg (rg)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS conta (
    numero INT NOT NULL AUTO_INCREMENT,
    cliente_cpf VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    saldo DECIMAL(15,2) NOT NULL,
    limite DECIMAL(15,2),
    montante_minimo DECIMAL(15,2),
    deposito_minimo DECIMAL(15,2),
    PRIMARY KEY (numero),
    UNIQUE KEY uk_conta_cliente (cliente_cpf),
    CONSTRAINT fk_conta_cliente
        FOREIGN KEY (cliente_cpf)
        REFERENCES cliente (cpf)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT ck_conta_tipo
        CHECK (tipo IN ('CORRENTE', 'INVESTIMENTO'))
) ENGINE=InnoDB AUTO_INCREMENT=1000;
