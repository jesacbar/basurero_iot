const express = require('express');
const app = express();
const path = require('path');
const morgan = require('morgan');
const mongoose = require('mongoose');

//conectar a la BD
mongoose.connect('mongodb://localhost/basurero-bd',  { useNewUrlParser: true }  ).
then(db=>console.log('Conectado a la base de datos'))
.catch(err=>console.log(err));

//importacion de rutas
const indexRoutes = require('./routes/index');

//configuraciones
app.set('port', process.env.PORT || 5000);
app.set('views', path.join(__dirname,'views'));
app.set('view engine', 'ejs');

//middlewares
app.use(morgan('dev'));
app.use(express.urlencoded({extended:false}));

//rutas
app.use(express.static(__dirname + '/public'));
app.use('/', indexRoutes);

//inciando server
app.listen(app.get('port'), ()=>{
    console.log(`Servidor escuchando en ${app.get('port')}` );
});

const http = require('http');
const WebSocketServer = require('websocket').server;

const server = http.createServer();
server.listen(7000);

const wsServer = new WebSocketServer({
    httpServer: server
});

wsServer.on('request', function(request) {
    const connection = request.accept(null, request.origin);

    connection.on('message', function(message) {
        console.log('Mensaje recibido: ', message.utf8Data);
        client.publish('basurero-iot-configuracion', message.utf8Data)
    });
    connection.on('close', function(reasonCode, description) {
        console.log('Se ha desconectado el cliente.')
    })
})

var mqtt = require('mqtt')
var client  = mqtt.connect('mqtt://test.mosquitto.org')
 
client.on('connect', function () {
  client.subscribe('basurero-iot-notificaciones', function (err) {
    if (!err) {
      console.log("Suscrito a servidor MQTT.");
    };
  });
});
 
client.on('message', function (topic, message) {
  console.log(message.toString());
  wsServer.broadcastUTF(message.toString());
});
