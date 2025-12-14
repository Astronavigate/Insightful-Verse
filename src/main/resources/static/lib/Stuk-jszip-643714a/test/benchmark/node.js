"use strict";

globalThis.Benchmark = require("static/lib/Stuk-jszip-643714a/test/benchmark/benchmark");
globalThis.JSZip = require("../../lib");

const benchmark = require("./benchmark");
benchmark("nodebuffer");
