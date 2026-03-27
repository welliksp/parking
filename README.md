# Parking Management System - Estapar

Sistema backend para gerenciamento de estacionamento com controle de vagas, entrada/saída de veículos, cálculo de receita e monitoramento em tempo real.

## 🚀 Tecnologias

- **Java 21**
- **Kotlin 2.2.21**
- **Spring Boot 4.0.4**
- **MySQL 9.6**
- **Flyway** (migrations)
- **Docker Compose**
- **Prometheus** (coleta de métricas)
- **Grafana** (visualização de dashboards)
- **Micrometer** (exposição de métricas)

## ✨ Funcionalidades

- ✅ Controle de entrada e saída de veículos
- ✅ Gestão de vagas por setor
- ✅ Precificação dinâmica baseada em ocupação
- ✅ Cálculo automático de tarifas
- ✅ Controle de lotação por setor
- ✅ Consulta de receita por setor e período
- ✅ Monitoramento em tempo real com Prometheus
- ✅ Dashboards interativos no Grafana
- ✅ Alertas automáticos de ocupação e erros
- ✅ Métricas de negócio customizadas
- ✅ Validação anti-duplicação de registros
- ✅ Integração com simulador de garagem
- ✅ Logs estruturados em português

## 📍 Pré-requisitos

- JDK 21
- Docker e Docker Compose
- Gradle (ou usar o wrapper incluído)

## 🔧 Configuração e Execução

### 1. Iniciar o Simulador da Garagem

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

O simulador estará disponível em `http://localhost:3000`

### 2. Iniciar os Serviços (Banco de Dados, Prometheus e Grafana)

```bash
docker compose up -d
```

Isso iniciará:
- **MySQL** na porta 3306
- **Prometheus** na porta 9090
- **Grafana** na porta 3001

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
    ├── metrics/        # Métricas e monitoramento
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
├── status (ENTERED, PARKED, EXITED)
└── active_key (controle de duplicação)
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

## 📊 Monitoramento e Métricas

### Prometheus

**URL**: http://localhost:9090

O Prometheus coleta métricas da aplicação a cada 15 segundos.

**Alertas Configurados**:
- ParkingAlmostFull - Estacionamento quase lotado (> 180 vagas)
- ParkingFull - Estacionamento lotado (>= 200 vagas)
- ApplicationDown - Aplicação fora do ar
- HighErrorRate - Taxa de erro elevada (> 0.1/s)
- HighMemoryUsage - Uso de memória > 90%
- SectorClosed - Setor fechado
- HighOccupancyRate - Ocupação > 90%

**Visualizar Alertas**: http://localhost:9090/alerts

### Grafana

**URL**: http://localhost:3001

**Credenciais**:
- Usuário: `admin`
- Senha: `admin`

**Dashboard**: http://localhost:3001/d/parking-dashboard/parking-management-system

**Painéis Disponíveis**:
1. **Ocupação Atual** - Gauge mostrando total de vagas ocupadas
2. **Entradas de Veículos** - Taxa de entradas por minuto
3. **Saídas de Veículos** - Taxa de saídas por minuto
4. **Receita Total** - Receita acumulada em BRL
5. **Taxa de Ocupação por Setor** - Percentual de ocupação (0-100%)
6. **Duração Média de Permanência** - Tempo médio em minutos
7. **Erros em Eventos** - Taxa de erros por tipo
8. **Health Status** - Status da aplicação (UP/DOWN)
9. **JVM Memory Used** - Uso de memória da JVM
10. **HTTP Requests** - Taxa de requisições HTTP

### Métricas de Negócio

| Métrica | Tipo | Descrição |
|---------|------|-----------|
| `parking_occupancy_current` | Gauge | Ocupação total atual |
| `parking_sector_occupancy` | Gauge | Ocupação por setor |
| `parking_sector_occupancy_rate` | Gauge | Taxa de ocupação (%) por setor |
| `parking_vehicle_entry_total` | Counter | Total de entradas |
| `parking_vehicle_exit_total` | Counter | Total de saídas |
| `parking_vehicle_parked_total` | Counter | Total de veículos estacionados |
| `parking_revenue_total` | Counter | Receita total por setor |
| `parking_duration_minutes` | Timer | Duração de permanência |
| `parking_event_error_total` | Counter | Erros em eventos |
| `parking_sector_open` | Gauge | Status de abertura do setor |

### Actuator Endpoints

```
http://localhost:3003/actuator/health
http://localhost:3003/actuator/metrics
http://localhost:3003/actuator/prometheus
```

### Exemplos de Queries PromQL

```promql
# Ocupação atual
parking_occupancy_current

# Taxa de entradas por minuto
rate(parking_vehicle_entry_total[5m]) * 60

# Receita total por setor
sum by (sector) (parking_revenue_total)

# Taxa de ocupação por setor
parking_sector_occupancy_rate
```

### Logs

Os logs estão padronizados em português com formato estruturado (key=value):

```
INFO  - Entrada de veículo processada: id=123, placa=ABC1234, setor=A, precoAplicado=10.00
DEBUG - Verificando disponibilidade do setor: setor=A, ocupadas=45, capacidade=50
INFO  - Saída de veículo processada: placa=ABC1234, duracao=120min, total=20.00
```

## 🔧 Utilitários

### Script para Liberar Vagas

Para liberar vagas ocupadas e testar o sistema:

```bash
./liberar_vagas.sh
```

O script:
- Libera 5 vagas do Setor A (placas SIM0001 a SIM0005)
- Libera 5 vagas do Setor B (placas SIM0011 a SIM0015)
- Exibe métricas atualizadas
- Aguarda 15 segundos para atualização das métricas

### Liberar Vaga Manualmente

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{"license_plate": "SIM0001", "exit_time": "2026-03-27T19:30:00.000Z", "event_type": "EXIT"}'
```

### Verificar Métricas

```bash
# Ver todas as métricas de negócio
curl http://localhost:3003/actuator/prometheus | grep "parking_"

# Ver taxa de ocupação por setor
curl http://localhost:3003/actuator/prometheus | grep "parking_sector_occupancy_rate"
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

### Dashboard do Grafana não mostra dados

1. Verifique se o Prometheus está coletando métricas:
```bash
curl http://localhost:9090/api/v1/targets
```

2. Verifique se as métricas estão sendo expostas:
```bash
curl http://localhost:3003/actuator/prometheus | grep parking_
```

3. Aguarde 15 segundos para o scheduler atualizar as métricas

### Todas as vagas estão ocupadas

Use o script para liberar vagas:
```bash
./liberar_vagas.sh
```

## 📚 Documentação Adicional

- [MONITORING.md](MONITORING.md) - Documentação completa de monitoramento
- [ACTUATOR.md](ACTUATOR.md) - Documentação dos endpoints do Actuator

## 📄 Licença

Este projeto foi desenvolvido como teste técnico para a Estapar.

## 👤 Autor

Desenvolvido como parte do processo seletivo para Desenvolvedor Java Back-end Pleno.
