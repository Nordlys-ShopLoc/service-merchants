# service-merchants

Microservice ShopLoc chargé des commerces partenaires, du catalogue et du click and collect.

## Rôle

Modules M2 et M3 du cahier des charges : référencement des commerces partenaires et de leurs horaires, catalogue d'articles et stocks, commandes click and collect et paiement. Ce service possède les données des commerces : l'inscription d'un commerce et sa validation par l'administrateur ShopLoc (US-S13, US-A01) y sont donc réalisées.

## Données

| Table | Contenu |
|---|---|
| `shops` | commerces partenaires |

Les migrations Flyway sont dans `src/main/resources/db/migration`. Un schéma déjà appliqué ne se modifie jamais : toute évolution passe par un nouveau fichier `V<n>__description.sql`.

## API

Accessible à travers la passerelle `api-gateway`.

| Méthode | Chemin | Rôle |
|---|---|---|
| GET | `/api/v1/shops` | liste des commerces partenaires |

## Architecture

```
fr.nordlys.shoploc.merchants
├── api/                         entrée HTTP : controllers, DTO
├── domain/                      métier pur, sans framework
│   ├── model/                   Shop…
│   ├── port/                    contrats de persistance
│   └── service/                 logique métier
└── infrastructure/persistence/  accès à PostgreSQL
    ├── entity/
    ├── repository/
    └── adapter/                 implémentations des ports
```

## Lancer le service en local

Prérequis : Java 21 et Docker Desktop démarré.

```bash
./mvnw spring-boot:test-run
```

Le service démarre sur http://localhost:8081 avec une base PostgreSQL temporaire, créée automatiquement dans Docker.

## Tests

```bash
./mvnw clean verify
```

## Image Docker

```bash
docker build -t service-merchants:local .
```

## Configuration

| Variable | Défaut |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/merchants` |
| `SPRING_DATASOURCE_USERNAME` | `merchants` |
| `SPRING_DATASOURCE_PASSWORD` | `merchants` |
| `SERVER_PORT` | `8081` |

## Contribuer

Une branche `feature/...` par user story, créée depuis `develop`. Commits au format Conventional Commits, en anglais. Pull request vers `develop`, relue par un autre membre, avec la CI au vert.
