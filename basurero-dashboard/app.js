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