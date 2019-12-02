const mongoose = require("mongoose");
const Esquema = mongoose.Schema;

const cambioEstadoEsquema = new Esquema({
    idBasurero: Number,
    fechahora: Date,
    llenadoAnterior: Number,
    llenadoActual: Number,
    volumenAnterior: Number,
    volumenActual: Number,
    estado: String
}, {collection: 'cambiosEstado'});

module.exports = mongoose.model("cambiosEstado", cambioEstadoEsquema);