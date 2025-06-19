# Sistema de Agência de Viagens em Java

Sistema de gerenciamento para agência de viagens com cadastro de clientes (nacionais/estrangeiros), pacotes turísticos e serviços. Desenvolvido em Java com MySQL.

## 🚀 Funcionalidades Principais

- **Clientes**
    - Cadastro de clientes nacionais (CPF) e estrangeiros (Passaporte)
    - Listagem completa de clientes
    - Busca por documento (CPF/Passaporte)
    - Contratação de pacotes turísticos

- **Pacotes Turísticos**
    - Cadastro com destino, duração e preço
    - Associação de serviços (ex: translado, passeios)
    - Listagem com serviços inclusos
    - Verificação de clientes que contrataram cada pacote

- **Serviços**
    - Cadastro de serviços adicionais (ex: seguro, passeios)
    - Listagem e busca por ID
    - Vinculação a pacotes turísticos

## ⚙️ Pré-requisitos

- Java JDK 11+
- MySQL 8.0+
- MySQL Connector/J (incluído no projeto)
- Biblioteca Lombok (para IDE compatível)