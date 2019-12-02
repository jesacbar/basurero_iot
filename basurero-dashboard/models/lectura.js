const mongoose = require("mongoose");
const Esquema = mongoose.Schema;

const lecturaEsquema = new Esquema({
    idBasurero: Number,
    fechahora: Date,
    altura: Number
});

module.exports = mongoose.model("lecturas", lecturaEsquema);