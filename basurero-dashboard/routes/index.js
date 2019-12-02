const express = require('express');
const router = express.Router();
const moment = require('moment')

const Basurero = require('../models/basurero');
const Lectura = require('../models/lectura');
const CambioLlenado = require('../models/cambioLlenado');
const CambioEstado = require('../models/cambioEstado')

router.get('/', async (req, res) => {
    var basureros = await Basurero.find().sort({ id: 1 });

    var datosBasureros = [];

    for (var basurero of basureros) {
        var idBasurero = basurero.id;
        var ultimoCambio = await CambioLlenado.findOne(
            { idBasurero: idBasurero }
        ).sort({ fechahora: -1 });
        // Llenado actual del bote
        var llenadoActual = ultimoCambio.llenadoActual;

        var ahora = moment();
        var fechahoraCambio = moment(ultimoCambio.fechahora)
        // Tiempo desde el último cambio de llenado.
        var tiempoUltimoCambio = ahora.diff(fechahoraCambio, 'minutes')

        var ultimoVaciado = await CambioEstado.findOne(
            {
                idBasurero: idBasurero,
                fechahora: { $lt: ahora },
                estado: 'VACIO'
            }
        ).sort({ fechahora: -1 });
        var fechahoraVaciado = moment(ultimoVaciado.fechahora);
        // Tiempo desde el último vaciado.
        var tiempoUltimoVaciado = ahora.diff(fechahoraVaciado, 'minutes');

        var haceUnDia = moment(ahora).subtract(1, 'days');
        var llenadosPorDia = await CambioEstado.find(
            {
                idBasurero: idBasurero, estado: "LLENO", fechahora: { $lt: ahora, $gt: haceUnDia }
            }
        );
        // Número de llenados en un día.
        var numLlenadosPorDia = Object.keys(llenadosPorDia).length

        var haceUnaSemana = moment(ahora).subtract(1, 'weeks');
        var llenadosPorSemana = await CambioEstado.find(
            {
                idBasurero: idBasurero, estado: "LLENO", fechahora: { $lt: ahora, $gt: haceUnaSemana }
            }
        );
        // Número de llenados en una semana.
        var numLlenadosPorSemana = Object.keys(llenadosPorSemana).length

        var haceUnMes = moment(ahora).subtract(1, 'months');
        var llenadosPorMes = await CambioEstado.find(
            {
                idBasurero: idBasurero, estado: "LLENO", fechahora: { $lt: ahora, $gt: haceUnMes }
            }
        );
        // Número de llenados en un mes.
        var numLlenadosPorMes = Object.keys(llenadosPorMes).length

        var vaciadosPorDia = await CambioEstado.find(
            {
                idBasurero: idBasurero, estado: "VACIO", fechahora: { $lt: ahora, $gt: haceUnDia }
            }
        );
        var volumenPorDia = 0;
        vaciadosPorDia.forEach(async function (vaciado) {
            volumenPorDia = volumenPorDia + vaciado.llenadoAnterior
        });
        // Volumen de basura vaciado por dia
        volumenPorDia = volumenPorDia / 100;

        var vaciadosPorSemana = await CambioEstado.find(
            {
                idBasurero: idBasurero, estado: "VACIO", fechahora: { $lt: ahora, $gt: haceUnaSemana }
            }
        );
        var volumenPorSemana = 0;
        vaciadosPorSemana.forEach(async function (vaciado) {
            volumenPorSemana = volumenPorSemana + vaciado.llenadoAnterior
        });
        // Volumen de basura vaciado por semana
        volumenPorSemana = volumenPorSemana / 100;

        var vaciadosPorMes = await CambioEstado.find(
            {
                idBasurero: idBasurero, estado: "VACIO", fechahora: { $lt: ahora, $gt: haceUnMes }
            }
        );
        var volumenPorMes = 0;
        vaciadosPorMes.forEach(async function (vaciado) {
            volumenPorMes = volumenPorMes + vaciado.llenadoAnterior
        });
        // Volumen de basura vaciado por mes
        volumenPorMes = volumenPorMes / 100;

        var cambiosPorDia = await CambioLlenado.find(
            {
                idBasurero: idBasurero, fechahora: { $lt: ahora, $gt: haceUnDia }
            }
        );
        var numCambiosPorDia = Object.keys(cambiosPorDia).length

        var datosBasurero = {
            idBasurero: idBasurero,
            llenadoActual: llenadoActual.toFixed(2),
            tiempoUltimoCambio: tiempoUltimoCambio,
            tiempoUltimoVaciado: tiempoUltimoVaciado,
            numLlenadosPorDia: numLlenadosPorDia,
            numLlenadosPorSemana: numLlenadosPorSemana,
            numLlenadosPorMes: numLlenadosPorMes,
            volumenPorDia: volumenPorDia.toFixed(2),
            volumenPorSemana: volumenPorSemana.toFixed(2),
            volumenPorMes: volumenPorMes.toFixed(2),
            numCambiosPorDia: numCambiosPorDia
        };

        datosBasureros.push(datosBasurero);
    };

    var sumaLlenadoTodos = 0;
    var volumenPorDiaTotal = 0;
    var volumenPorSemanaTotal = 0;
    var volumenPorMesTotal = 0;
    var basureroMasUsado = "";
    var mayorNumCambiosPorDia = 0;
    for (var datosBasurero of datosBasureros) {
        sumaLlenadoTodos += parseFloat(datosBasurero.llenadoActual);
        volumenPorDiaTotal += parseFloat(datosBasurero.volumenPorDia);
        volumenPorSemanaTotal += parseFloat(datosBasurero.volumenPorSemana);
        volumenPorMesTotal += parseFloat(datosBasurero.volumenPorMes);
        if (datosBasurero.numCambiosPorDia > mayorNumCambiosPorDia) {
            basureroMasUsado = "Basurero #" + datosBasurero.idBasurero;
            mayorNumCambiosPorDia = datosBasurero.numCambiosPorDia;
        }
    }
    var promedioLlenadoTodos = sumaLlenadoTodos / datosBasureros.length;

    var datosGenerales = {
        promedioLlenadoTodos: promedioLlenadoTodos.toFixed(2),
        volumenPorDiaTotal: volumenPorDiaTotal.toFixed(2),
        volumenPorSemanaTotal: volumenPorSemanaTotal.toFixed(2),
        volumenPorMesTotal: volumenPorMesTotal.toFixed(2),
        basureroMasUsado: basureroMasUsado
    };

    res.render('index', {
        datosBasureros,
        datosGenerales
    });
});

module.exports = router;