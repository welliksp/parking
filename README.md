# Parking Management System - Estapar

Sistema backend para gerenciamento de estacionamento com controle de vagas, entrada/saída de veículos e cálculo de receita.

## 🚀 Tecnologias

- **Java 21**
- **Kotlin 2.2.21**
- **Spring Boot 4.0.4**
- **MySQL 9.6**
- **Flyway** (migrations)
- **Docker Compose**

## 📋 Pré-requisitos

- JDK 21
- Docker e Docker Compose
- Gradle (ou usar o wrapper incluído)

## 🔧 Configuração e Execução

### 1. Iniciar o Simulador da Garagem

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

O simulador estará disponível em `http://localhost:3000`

### 2. Iniciar o Banco de Dados

```bash
docker-compose up -d
```

Isso iniciará o MySQL na porta 3306.

### 3. Executar a Aplicação

```bash
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:3003`

## 📡 Endpoints

### Webhook (Recebe eventos do simulador)

**POST** `http://localhost:3003/webhook`

#### Evento ENTRY
```json
{
  "license_plate": "ABC1234",
  "entry_time": "2025-01-01T12:00:00.000Z",
  "event_type": "ENTRY"
}
```

#### Evento PARKED
```json
{
  "license_plate": "ABC1234",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}
```

#### Evento EXIT
```json
{
  "license_plate": "ABC1234",
  "exit_time": "2025-01-01T14:30:00.000Z",
  "event_type": "EXIT"
}
```

### Consulta de Receita

**POST** `http://localhost:3003/revenue`

Request:
```json
{
  "date": "2025-01-01",
  "sector": "A"
}
```

Response:
```json
{
  "amount": 150.00,
  "currency": "BRL",
  "timestamp": "2025-01-01T12:00:00.000Z"
}
```

## 📊 Regras de Negócio

### Precificação Dinâmica (na entrada)

| Lotação | Multiplicador | Exemplo (base R$ 10,00) |
|---------|---------------|-------------------------|
| < 25% | 0.90 (-10%) | R$ 9,00 |
| 25-50% | 1.00 (0%) | R$ 10,00 |
| 50-75% | 1.10 (+10%) | R$ 11,00 |
| 75-100% | 1.25 (+25%) | R$ 12,50 |

### Cálculo de Tarifa (na saída)

- **Primeiros 30 minutos**: Grátis
- **Após 30 minutos**: Tarifa por hora (arredondada para cima)
  - Exemplo: 45 minutos = 1 hora
  - Exemplo: 90 minutos = 2 horas

### Controle de Lotação

- Setor fecha automaticamente ao atingir 100% de ocupação
- Setor reabre automaticamente quando uma vaga é liberada

## 🏗️ Arquitetura

O projeto segue **Arquitetura Hexagonal (Ports & Adapters)**:

```
src/main/kotlin/br/com/wsp/parking/
├── domain/              # Regras de negócio
│   ├── model/          # Entidades de domínio
│   ├── service/        # Serviços de domínio
│   ├── port/           # Interfaces (ports)
│   └── exception/      # Exceções de domínio
├── app/                # Casos de uso
│   └── usecase/        # Implementação dos casos de uso
└── infra/              # Infraestrutura
    ├── web/            # Controllers REST
    ├── persistence/    # Adaptadores JPA
    └── client/         # Clientes HTTP
```

## 🗄️ Banco de Dados

### Migrations (Flyway)

As migrations são executadas automaticamente na inicialização:

- `V1__Create_sectors_table.sql` - Tabela de setores
- `V2__Create_spots_table.sql` - Tabela de vagas
- `V3__Create_parking_records_table.sql` - Tabela de registros

### Modelo de Dados

```
sectors
├── name (PK)
├── base_price
├── max_capacity
├── open_hour
├── close_hour
├── duration_limit_minutes
└── is_open

spots
├── id (PK)
├── sector_name (FK)
├── lat
├── lng
└── occupied

parking_records
├── id (PK)
├── license_plate
├── sector_name (FK)
├── spot_id (FK)
├── entry_time
├── exit_time
├── applied_price
├── total_amount
└── status (ENTERED, PARKED, EXITED)
```

## 📝 Documentação da API

Acesse a documentação interativa (Swagger UI):

```
http://localhost:3003/swagger-ui.html
```

## 🧪 Testes

```bash
./gradlew test
```

## 📊 Monitoramento

### Actuator Endpoints

```
http://localhost:3003/actuator/health
```

### Logs

Os logs seguem formato estruturado (key=value) para facilitar análise:

```
INFO  - Vehicle entry processed: id=123, licensePlate=ABC1234, sector=A, appliedPrice=10.00
DEBUG - Checking sector availability: sector=A, occupied=45, capacity=50
```

## 🔍 Troubleshooting

### Simulador não envia eventos

Verifique se a aplicação está rodando na porta 3003:
```bash
curl http://localhost:3003/actuator/health
```

### Erro de conexão com MySQL

Verifique se o container está rodando:
```bash
docker-compose ps
```

### Configuração da garagem não carregada

Verifique se o simulador está acessível:
```bash
curl http://localhost:3000/garage
```

## 📄 Licença

Este projeto foi desenvolvido como teste técnico para a Estapar.

## 👤 Autor

Desenvolvido como parte do processo seletivo para Desenvolvedor Java Back-end Pleno.
