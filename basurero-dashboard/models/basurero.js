const mongoose = require("mongoose");
const Esquema = mongoose.Schema;

const basureroEsquema = new Esquema({
    id: Number,
    altura: Number,
    volumen: Number
});

module.exports = mongoose.model("basureros", basureroEsquema);