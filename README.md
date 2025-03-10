# bills-system
 Simple REST API for storing and retrieving bills

Este projeto foi desenvolvido em Java utilizando Spring Boot. A aplicação, o banco de dados e outros serviços necessários estão orquestrados utilizando Docker Compose.

Tecnologias Utilizadas:
Java
Spring Boot
Docker
Docker Compose
PostgreSQL
Flyway
Spring Security e JWT
Swagger

# Autenticação

Autenticação e Autorização: O projeto utiliza Spring Security e JWT para autenticação e autorização. Usuários podem se registrar e fazer login para receber um token JWT, que deve ser usado para acessar endpoints protegidos.

# Migrações

Migrações de Banco de Dados: Utilizamos Flyway para gerenciar as migrações de banco de dados.

# Documentação

Documentação da API: A documentação da API é gerada automaticamente utilizando Swagger para visualizar e testar os endpoints da API diretamente no navegador.
Testes: Os testes unitários foram implementados utilizando JUnit e Mockito.

# Instruções para Construir e Rodar a Aplicação

Instruções para Construir e Rodar a Aplicação
### Pré-requisitos
Docker
Docker Compose

Como Construir e Rodar

Clone o repositório:

git clone https://github.com/Tenshyn/bills-system.git

cd bills-system

### Construa e inicie os contêineres:

docker-compose up --build

### Verificação das Imagens Geradas

Para verificar se as imagens foram geradas corretamente, você pode usar o seguinte comando:
docker images


## Serviços e Portas

Aplicação: http://localhost:8080/api
PostgreSQL: localhost:5432

## Acesso ao Swagger

Após iniciar os contêineres, você pode acessar a documentação da API através do Swagger:
http://localhost:8080/api/swagger-ui/index.html

Todos os endpoints estão documentados no swagger, incluindo os payloads e respostas.

## Importação por arquivo CSV

É possível importar contas para o sistema utilizando um arquivo CSV, as especificações do arquivo estão no swagger e também há um arquivo de exemplo que pode ser utilizado para testes dentro de resources
