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

[Imagem operator DG]

Mantenha as configurações padrão e clique no botão Install. Após finalizar a instalação, clique no botão View Operator para acessar a tela do operator.

[Tela operator DG]

Em seguida vamos criar as instâncias necessárias usando os yamls disponibilizados nesse repositório em * caminho *.

OBS.: No arquivo infinispan.yaml deve ser usada a rota do host do cluster OpenShift. 

Por exemplo:
console-dg.<cluster-hostname>
console-dg.apps.cluster-g6kn4.g6kn4.sandbox1680.opentlc.com

Clique em Create Instance na opção Infinispan Cluster e cole o yaml correspondente (acesse aqui), e clique no botão Create.

[Imagem infinispan yaml]

Perceba que serão criados 3 pods para o cluster.

[Imagem pods infinispan]

O mesmo deve ser feito para a opção Cache.Cole o yaml correspondente (acesse aqui), e clique no botão Create.

[Imagem cache yaml]

Repare que temos 3 caches criados. Esse comportamento é normal, pois os caches memcached-cache e resp-cache são criados automaticamente pelo cluster infinispan. O importante é que eles estejam com o status Ready.

[Imagem cache]

Um ponto interessante é que será habilitada uma rota para acessar a console do Red Hat Data Grid. Vá para o menu lateral esquerdo e acesse Networking, e depois Routes. Por fim, clique no link Location da rota infinispan-external.

A console irá pedir o usuário e senha de acesso. Essas credenciais se encontram em uma Secret chamada infinispan-generated-secret. Vá para o menu lateral esquerdo e acesse Workloads e depois Secrets.

Após informar as credenciais corretamente, clique no botão Open the console.

[Painel DG]

Finalizamos essa etapa.

### Projeto application

Após criar esse projeto, vamos provisionar o nosso banco de dados, um MySQL.

Na perspectiva Developer, vamos usar uma solução de template já definida para a criação do banco de dados. No menu lateral esquerdo, clique em +Add.


Dentre as opções apresentadas, escolha Database dentro do painel Developer Catalog.

[Imagem +Add]

Vamos escolher a opção MySQL - Provided by Red Hat, Inc. Clique no botão "Instantiate Template"

[Imagem Database]

No formulário, precisamos definir alguns parâmetros para a configuração do banco. Segue abaixo a relação de parâmetro e valor a ser ajustado:

|MySQL Connection Username |Defina o nome que achar mais adequado|
|MySQL Connection Password | Defina a senha que achar mais adequada|
|MySQL root user Password |Defina a senha que achar mais adequada|
|MySQL Database Name| presentation_db|
|Version of MySQL Image | 8.0-el7|
|Clique no botão create|

[Imagem form database]

Esse processo é rápido, ainda que esteja criando recursos como StatefullSet, Secrets e ConfigMaps. Como resultado devemos ver o pod em status Running (argola azul)

[Imagem pod criado]