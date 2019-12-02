const mongoose = require("mongoose");
const Esquema = mongoose.Schema;

const cambioLlenadoEsquema = new Esquema({
    idBasurero: Number,
    fechahora: Date,
    llenadoAnterior: Number,
    llenadoActual: Number,
    volumenAnterior: Number,
    volumenActual: Number,
    estado: String
}, {collection: 'cambiosLlenado'});

module.exports = mongoose.model("cambiosLlenado", cambioLlenadoEsquema);