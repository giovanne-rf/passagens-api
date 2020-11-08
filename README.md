# ETC - APP -  COMPRA E VENDA DE PASSAGENS AÉREAS

ROTA  - Heroku: https://passagem-app.herokuapp.com/swagger-ui.html
ROTA - Azure: https://etc-app.azurewebsites.net/swagger-ui.html (o serviço foi interrompido em virtude da expiração dos dados e eminente cobrança da assinatura, mas houve o deploy que documentamos com videos e fotos)

Metodologia Agil

Inicialmente tivemos uma sprint para definir quais tecnologias seriam necessárias
para o desenvolvimento da API, desde a definição de banco até as API's de terceiros para consumo e realizamos sprints diários (em virtude do tempo curtíssimo), com reuniões para revisão, acompanhamento e ajustes das tarefas e prazos.

A API foi pensada exautivamente antes de iniciar a sua codificação, erros foram previstos e aplicações possíveis testadas.

Logo após isso estávamos preparados para começar o desenvolvimento.

Em uma das reunições de pesquisa e estratégias, foi detectado uma aplicação chamada Amadeus que contem dados mundiais de passagens aéreias e é amplamente utilizado no mercado de compra e venda de passagens aéreas e ele fpi utilizado para o recebimento de dados dos voos com itinerários e preços, por quê oferece uma documentação
robusta sobre como consumir a API.

Foi feito uma rota para que fosse possivel efetuar o login de usuário.

Após iniciar o desenvolvimento da API ocorreram problemas com o grande número de dados que a API do amadeus nos retornava no documento JSON.

Foi feito a abstração de informações retornadas no formato JSON, para que fossem utilizadas apenas os atributos pertinentes ao desenvolvimento.

Logo após ser resoLvido o problema de abstração dos dados da API amadeus, fomos ao próximo passo, integrar a API de pagamenos, Pagar.me, onde foi feita
a autenticação e integração do método de pagamento das consultas efetuadas no Amadeus.

Após isso foi feito o deploy no heroku, pois ocorreram problemas no deploy na Azure, conforme o erro abaixo.

ERRO:Failed to execute goal com.microsoft.azure:azure-webapp-maven-plugin:1.11.0:deploy (default-cli) on project spring-boot:
This region has quota of 0 PremiumV2 instances for your subscription. Try selecting different region or SKU.
OnError while emitting onNext value: retrofit2.Response.class -> [Help 1]


# INFORMAÇÕES TÉCNICAS PARA CONSUMO DA API ETC

API - Para Gerenciamento de Passagens Aereas

Ao iniciar  a aplicação pela classe PassagensApiApplication sera realizada a criação das tabelas de dominio
e a inclusão de 2 perfis de acesso ADMIN e USER, além da inclusão do Usuário MASTER (admin:admin).


Você pode validar a autenticação do usuário MASTER através da documentação: https://passagem-app.herokuapp.com/swagger-ui.html#

POST: http://{HOST}:{PORTA}/login
{
  "senha": "admin",
  "usuario": "admin"
}

Response:

{
  "login": "admin",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInNjb3BlcyI6IlJPTEVfQURNSU4iLCJpYXQiOjE2MDQ3MDI4MjEsImV4cCI6MTYwNDcwNjQyMX0.cAY_X-VnqogmYFDZ3E2v4YKC_1qhGT7R8gAWQAQE1gc",
  "userId": 1
}

Se preferir está disponível a inclusão dos viajantes (clientes) ou consulta dos voos pela API.

VOSS

POST: http://{HOST}:{PORTA}/flights
{
	"originLocationCode" : "NYC",
	"destinationLocationCode" : "PAR",
	"departureDate" : "2020-12-01" ,
	"returnDate" : "2020-12-06",
	"adults" : "1",
	"max":"2"
}

Response:

"data": [
    {
      "response": null,
      "deSerializationClass": null,
      "type": "flight-offer",
      "id": "1",
      "source": "GDS",
      "instantTicketingRequired": false,
      "nonHomogeneous": false,
      "oneWay": false,
      "lastTicketingDate": "2020-11-06",
      "numberOfBookableSeats": 3,
      "itineraries": [
	  
	  ...
]


Para prosseguir na iteração da API é necessário realizar o cadastro do viajante:


POST: http://{HOST}:{PORTA}/viajantes

"cpfCnpj": "1234",
  "dataNascimento": "1990-10-10",
  "documento": {
    "numeroDocumento": "123123",
    "tipoDocumento": "PASSPORT"
  },
  "email": "viajante@gmail.com",
  "login": "test",
  "senha": "test",
  "nome": "VIAJANTE TEST",
  "sexo": "M",
  "telefone": {
    "ddd": 11,
    "nomeContato": "teste",
    "numero": 89980998
  }
  
  

Agora é o momento de realizar a geração da reserva (order)


POST: http://{HOST}:{PORTA}/flights/order/{viajante} - Onde o viajante é o id do cadastro

Para exemplo utilizar o arquivo createOrder.txt como body da requisição


Response:
"data": {
    "type": "flight-order",
    "id": "eJzTd9f39QlxtTAGAAs9AkM%3D",
    "associatedRecords": [
	...
	]

Além da resposta é armezado dados relevantes em nossa base de dados como id, preço e data da reserva.

A partir agora, para consultar as reservas é necessário realizar o login como ADMIN ou Viajante acessando a url de login para obter o token

GET: http://{HOST}:{PORTA}/reservas

Parametros:
inicio: 2020-11-01 00:00:00
fim: 2020-11-30 23:59:59
viajanteId: 3

{
  "viajante": {
    "id": 3,
    "nome": "VIAJANTE TEST",
    "cpfCnpj": "1234",
    "email": "viajante1@gmail.com",
    "telefone": {
      "ddd": 11,
      "numero": 89980998,
      "nomeContato": "teste"
    },
    "documento": {
      "numeroDocumento": "123123",
      "tipoDocumento": "PASSPORT"
    },
    "dataNascimento": "1990-10-10",
    "sexo": "M"
  },
  "reservas": [
    {
      "id": 1,
      "orderId": "eJzTd9f39QlxtTAGAAs9AkM%3D",
      "price": 100,
      "viajanteId": 3,
      "dataHora": "2020-11-06T20:12:41",
      "status": "PP"
    }
  ]
}

As reservas por padrão estarão como PP (Pendentes de pagamentos), para integrar a Reserva com uma API de pagamentos (Pagarme), é necessário realizar a chamada conforme abaixo:

GET: http://{HOST}:{PORTA}/reservas/pagamento

Parametros:
orderId: {orderId}

Response:
{
  "viajante": {
    ....
  },
  "reservas": [
    {
      "id": 1,
      "orderId": "QlxtTAGAAs9AkM%3D",
      "price": 100,
      "viajanteId": 3,
      "dataHora": "2020-11-06T21:12:41",
      "status": "PG",
      "idPagamento": 10287154
    }
  ]
}

NOTA: Se houve a confirmação do pagamento o status da reserva estará como PG (Pago) e o id do pagamento de api de pagamentos.



