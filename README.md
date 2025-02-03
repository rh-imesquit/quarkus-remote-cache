# quarkus-remote-cache


## Tecnologias utilizadas

Esse laboratório foi executado com as seguintes espeificações:

- OpenShift 4.16
- Red Hat Data Grid Operator 8.5.4
- MySQL 8.0
- Quarkus 3.18.1 (Java 17)

## Pré Requisitos

Antes de começar, é necessário ter acesso ao OpenShift, pois todo o laboratório será realizado por meio dele. O usuário deverá estar devidamente autenticado.


## Provisionando a infraestrutura necessária

Vamos começar criando dois projetos no OpenShift para criar uma camada de isolamento lógico entre as partes da nossa solução.
- remote-cache
- application

### Projeto remote-cache

Após criar esse projeto, vamos instalar o operator do Red Hat Data Grid. Na perspectiva Administrador, acesse o menu lateral esquerdo Operators e clique em OperatorHub. Na barra de busca, procure por Red Hat Data Grid.

![Instalação do operator Data Grid](/images/operator-data-grid.png)

Mantenha as configurações padrão e clique no botão Install. Após finalizar a instalação, clique no botão View Operator para acessar a tela do operator.

![Detalhes do operator Data Grid](/images/operator-data-grid-details.png)


Em seguida vamos criar as instâncias necessárias usando os yamls disponibilizados nesse repositório [aqui](/infra/openshift/data-grid/).

OBS.: No arquivo infinispan.yaml deve ser usada a rota do host do cluster OpenShift. 

Por exemplo:
console-dg.<cluster-hostname>
console-dg.apps.cluster-g6kn4.g6kn4.sandbox1680.opentlc.com

Clique em Create Instance na opção Infinispan Cluster e cole o yaml correspondente (acesse aqui), e clique no botão Create.

![Definição de yaml do infinispan](/images/infinispan-yaml.png)

Perceba que serão criados 2 pods para o cluster.

![Pods do infinispan](/images/infinispan-pods.png)

O mesmo deve ser feito para a opção Cache.Cole o yaml correspondente (acesse aqui), e clique no botão Create.

![Definição de yaml do cache](/images/cache-yaml.png)

Repare que temos 3 caches criados. Esse comportamento é normal, pois os caches memcached-cache e resp-cache são criados automaticamente pelo cluster infinispan. O importante é que eles estejam com o status Ready.

![Pods do cache](/images/cache-pods.png)

Um ponto interessante é que será habilitada uma rota para acessar a console do Red Hat Data Grid. Vá para o menu lateral esquerdo e acesse Networking, e depois Routes. Por fim, clique no link Location da rota infinispan-external.

A console irá pedir o usuário e senha de acesso. Essas credenciais se encontram em uma Secret chamada infinispan-generated-secret. Vá para o menu lateral esquerdo e acesse Workloads e depois Secrets.

Após informar as credenciais corretamente, clique no botão Open the console.

![Tela de autenticação do Data Grid](/images/dg-console-auth.png)

![Painel do Data Grid](/images/dg-console-panel.png)


Finalizamos essa etapa.

### Projeto application

#### Banco de dados

Após criar esse projeto, vamos provisionar o nosso banco de dados, um MySQL.

Na perspectiva Developer, vamos usar uma solução de template já definida para a criação do banco de dados. No menu lateral esquerdo, clique em +Add.


Dentre as opções apresentadas, escolha Database dentro do painel Developer Catalog.

![Painel +Add](/images/developer-perspective-add.png)

Vamos escolher a opção MySQL - Provided by Red Hat, Inc. Clique no botão "Instantiate Template"

![Banco de dados MySQL](/images/mysql-db-install.png)

No formulário, precisamos definir alguns parâmetros para a configuração do banco. Segue abaixo a relação de parâmetro e valor a ser ajustado:

| Parameter | Value |
| ----- | ----- |
|MySQL Connection Username |Defina o nome que achar mais adequado|
|MySQL Connection Password | Defina a senha que achar mais adequada|
|MySQL root user Password |Defina a senha que achar mais adequada|
|MySQL Database Name| presentation_db|
|Version of MySQL Image | 8.0-el7|

**Por fim, clique no botão create**

![Formulário do banco de dados MySQL](/images/mysql-db-form.png)

Esse processo pode levar alguns poucos minutos para concluir. Como resultado devemos ver o pod em status Running (círculo azul)

![Pod do MySQL 8](/images/mysql-pod.png)


#### Aplicações

Vamos fazer o deploy das aplicações usando a estratégia Source to Image (S2I) do Red Hat OpenShift com base no nosso repositório Git.

Para começar, vamos faze o deploy do nosso microsserviço de apresentações. Na perspectiva Developer, acesse o menu lateral esquerdo, clique em +Add. 

Dentre as opções apresentadas, escolha Import from Git dentro do painel Git Repository.

Vamos preencher o formulário com os seguintes parâmetros:

| Parâmetro | Value |
| ----- | ----- |
| Git Repo URL | https://github.com/rh-imesquit/quarkus-remote-cache |
| Context dir | /apps/quarkus-presentation-ms |
| Application name | quarkus-presentation-ms |
| Name | quarkus-presentation-ms |

Na seção Deploy, clique no link "Show advanced Deployment option" e preencha as seguintes variáveis de ambiente

| Environment Variable | Tipo | Value |
| ----- | ----- | ----- |
| DB_NAME | Secret | mysql - database-name|
| DB_USER | Secret | mysql - database-user|
| DB_PASSWORD | Secret | mysql - database-password|
| DB_HOST | Text | Hostname gerado para o service do MySQL |

**Por fim, clique no botão create**

![Importando o microsserviço de apresentações do git](/images/import-git-microservice.png)

Como resultado devemos ver o pod da aplicação quarkus-presentation-ms em status Running (círculo azul)

![Pod do microsserviço de apresentações](/images/presentation-microservice-running.png)

Vamos fazer algumas requisições para ver se o serviço está funcionando de fato. Não esqueça de buscar a rota gerada para a aplicação no menu lateral Networking > Routes. No terminal do seu computador execute os seguintes comandos:

```
curl -X GET <Rota gerada para a aplicação >/presentation && echo
```

O resultado deve ser: *{"message": "There are no registered presentations"}*.

Vamos inserir um registro na base desse microsserviço.

```
curl -X POST <Rota gerada para a aplicação>/presentation \
     -H "Content-Type: application/json" \
     -d '{
           "theme": "Quarkus e Red Hat Data Grid",
           "author": "Ian Mesquita",
           "dateTime": "2025-02-07T16:00:00"
         }'  && echo
```

O resultado deve ser: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus e Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}*.

Nesse momento o registro está inserido no banco de dados.

Vamos buscar esse registro individualmente para observar um comportamento esperado.

```
curl -X GET <Rota gerada para a aplicação >/presentation/1 && echo
```
O resultado deve ser: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus e Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}*.

Note que o resultado demorou a aparecer, e está certo. É isso mesmo! Esse endpoint foi implementado para adicionar uma simulação de um backend que demora cerca de 8 segundos para responder.

Vamos para a última parte desse laboratório.

Agora seguindo o mesmo procedimento, vamos fazer o deploy do nosso microsserviço quarkus-infinispan-cache. Antes de mais nada, vamos criar uma secret com as credenciais de autenticação ao Infinispan. Na perspectiva Administrator, acesse o menu lateral Workloads e em seguida Secrets. Clique no botão Create e em seguida Key/Value secret.

![Criando a secret com credenciais do infinispan](/images/infinispan-secret-creation.png)

Adicione 3 key/values options e os defina como o quadro abaixo:

| Parâmetro | Value |
| ----- | ----- |
| Secret name | infinispan |
| Key 1 | cache-host |
| Value 1 | <Host gerado para o Service do Infinispan (remote-cache project)> |
| Key 2 | cache-user |
| Value 2 | <Usuário gerado no secret infinispan-generated-secret (remote-cache project)> |
| Key 3 | cache-password |
| Value 3 | <Senha gerada no secret infinispan-generated-secret (remote-cache project)> |

Na perspectiva Developer, acesse o menu lateral esquerdo, clique em +Add. 

Dentre as opções apresentadas, escolha Import from Git dentro do painel Git Repository.

Vamos preencher o formulário com os seguintes parâmetros:

| Parâmetro | Value |
| ----- | ----- |
| Git Repo URL | https://github.com/rh-imesquit/quarkus-remote-cache |
| Context dir | /apps/quarkus-infinispan-cache |
| Application name | quarkus-infinispan-cache |
| Name | quarkus-infinispan-cache |

Na seção Deploy, clique no link "Show advanced Deployment option" e preencha as seguintes variáveis de ambiente

| Environment Variable | Tipo | Value |
| ----- | ----- | ----- |
| INFINISPAN_HOST | Secret | infinispan - cache-host|
| INFINISPAN_USER | Secret | infinispan - cache-user|
| INFINISPAN_PASSWORD | Secret | infinispan - cache-password|
| API_PRESENTATION_URL | Text | Rota gerada para o microserviço quarkus-presentation-ms |

**Por fim, clique no botão create**

Como resultado devemos ver o pod da aplicação quarkus-infinispan-cache em status Running (círculo azul)

![Pod do microsserviço de cache](/images/presentation-cache- microservice-running.png)

Vamos testar o conjunto, mas antes recupere a Rota criada para o microsseviço de cache no menu lateral Networking > Routes.

Primeiro, vamos testar o endpoint sem o cache e medir o tempo de resposta.

```
time curl -X GET <Rota gerada para o microsserviço quarkus-infinispan-cache>/presentation/nocache/1 && echo
```

O resultado deve ser algo aproximado a: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus e Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}
real	0m9.013s
user	0m0.015s
sys	0m0.010s*.

Exatamente como o esperado, o microsserviço de apresentações está lento.

Antes de seguir em frente, olhe no painel do console do Red Hat Data Grid que não temos entradas no cache presentations.

![Detalhes do cache presentation](/images/presentation-cache-details.png)

Agora, vamos usar o endpoint com o cache infinispan implementado. A primeira execução irá demorar pois a entrada ainda não está no cache, como vimos. Mas nas próximas execuções, o tempo cairá consideravelmente. 
```
time curl -X GET <Rota gerada para o microsserviço quarkus-infinispan-cache>/presentation/cache/1 && echo
```

O resultado deve ser algo aproximado a: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus e Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}
real	0m0.548s
user	0m0.016s
sys	0m0.004s*.

Agora estamos acessando o cache. 

Acesse o painel do Data Grid e veja que a entrada está registrada no cache com o TTL (Time to Live) de 1800 segundos, ou seja, 30 minutos.

![Detalhes do cache presentation](/images/cache-populated.png)

Bom trabalho! Finalizamos nosso lab.